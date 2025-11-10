# portfolioApp_SKILLSYNC_IAC_AWS_Java_Scaling_Microservices

This readme will be cleaned up further down the line...

portfolio project to demonstrate: 

- IAC within AWS that will be developed mostly locally using localstack but can be deployed to AWS as full cloud app 
  - isolated dev environment, infrastructure versioning, rollbacks
- AUTHn: AWS cognito JWTs
  - avoid rolling own security 
  - identity persisted client side
- stateless Spring/Springboot REST API microservices represented 
  - each type of micro service access same mysql datastore for any persisted data, 
  - each request handled by microservice is completely stateless and independent 
  - partial persistence in DB but will eventually use REDIS/Caching
- Authz: handled by microservice instances themselves
  - (finished transactions can only be revisisted by registered users who were
- scalability through Message-based microservice architectures  
- clean software engineering


## development phases
1. single springboot UserService REST API from docker container, no DB
2. same as above but add data persistence with mysql docker container and spring-boot-starter-data-jpa
  - docker compose for both containers along with any environmental config
3. proper software layering / separation of concerns for enterprise springboot app
  - controller layer (thin): only http requests, validation, auth, maybe mapping DTOs later
  - service layer: business logic requiring orchestration of multiple entities, or any external systems. wrap in @Transactional
  - repository layer: persistence. database operations. CRUD operations on multiple instances of an entity, queries, paging
  - entity/model layer: JPA entities mapped to database tables. state/behavior that does not need repsitory or external systems. behavior that belongs to single entity itself 
  - (not a layer) have exception handling done with global @ControllerAdvice annotation
4.  add another service with its own db docker instances: TransactionService. have (temporary) synchronous service-to-service communication via REST (creating transaction from userIds requires looking up first name and last name from UserService). add unit tests on entities using Junit and and endpoint tests at this stage.  
5. introduce localstack integration. have API gateway route to microservices. start building cloudformation template or SDK code here. manual deployment for now. 
6. login flow and authentication with actual AWS cognito userpool. cognito generates JWT that is saved client side. jwt validation in springboot microservices for authorization (only users in transaction can request transaction)
7. stateful workflows in stateless architecture. for persisting data, handle in-progress data with multi-step operations: either draft records, partial updates, finalized record, or use external caching store like REDIS. (investigate optimistic locking and status fields)
8. add email service, SNS for event distribution, SQS for all 3 microservices, and create a flow for finalizing transactions that involves both synchronous response to client and asynchronous emailing 



[ User (Browser/App) ]
        │
        ▼
[ Cognito Auth (JWT) ]
        │
        ▼
[ API Gateway ]
        │
        ▼
[ Transaction Service ]
        │
        ▼
 Publishes → [ SNS Topic: transaction-events ]
                  │
       ┌──────────┼──────────┐
       ▼          ▼          ▼
[ Notification Q ] [ User Q ] [ Analytics Q ]
       │          │          │
       ▼          ▼          ▼
[ NotificationSvc ][UserSvc][AnalyticsSvc]






### copilot walkthrough on request/scaling using the initialExploratory cloudformation template

Below is a concise walkthrough of how a request moves through your stack and how scaling/autoscaling, queues and topics, and ECS concepts relate. I’ll call out important operational details and recommended signals for autoscaling, retries and failure handling.

High-level request path (HTTP request)
Client (browser/mobile) → API Gateway (HTTP API)
Auth optionally validated here (Cognito JWT authorizer).
Gateway forwards to ALB (HTTP proxy / VPC Link) or directly to service.
ALB → Target Group → ECS Service (Fargate tasks)
ALB routes by path (e.g., /users → users target group).
Health checks on target group determine which tasks receive traffic.
ECS Service → Task → Container
The Task is an instance of the Task Definition (container image, ports, env, secrets, roles).
Container handles request, calls repositories, other services, DB, caches, or publishes events.
Where the DB and secrets fit
Task uses Task Role to call AWS APIs (SQS, SNS, SecretsManager) and Execution Role for pulling images/logging.
SecretsManager values (DB creds) are injected into task as secrets or mounted env vars.
Service code uses Hikari/DataSource to connect to RDS. Connections are pooled per task; connection count = tasks * pool size → size pools appropriately.
Async/event-driven interactions (SNS/SQS)
Service A (e.g., transaction service) publishes an event to SNS topic (transaction-events).
SNS fans out to SQS queues (user-queue, notification-queue, analytics-queue).
Consumers:
Dedicated consumer tasks (could be separate ECS service / worker) poll SQS and process messages (email worker, analytics sink).
Alternatively, the Email microservice may expose an API and also subscribe to the queue or be invoked via HTTP from the producer.
SQS behavior:
Long polling, visibility timeout, retries, and optional DLQ for failed messages.
Use queue length and age as autoscaling signal for worker services.
Autoscaling primitives & signals
HTTP services (ECS Service for users/transactions):
Scale-out signals: ALB target tracking (average CPU, request count per target), custom CloudWatch metric (CPU, memory, request latency).
Configure Service Auto Scaling (target tracking policy, step scaling).
Health checks + deregistration delay ensure graceful draining.
Background workers (SQS consumers):
Scale on SQS ApproximateNumberOfMessagesVisible (or messages per worker) using Application Auto Scaling.
Use concurrency limits and small visibility timeouts to prevent duplicates.
Database scaling:
RDS vertical/horizontal scaling or read replicas for read-heavy loads. Ensure connection pool size per task is tuned, or use RDS Proxy for connection multiplexing.
Deployment & lifecycle
Task Definition versioned; ECS Service triggers new tasks when TaskDefinition changes.
Deployments:
Rolling update: new tasks start, pass health checks, ALB drains old tasks.
Blue/green possible with CodeDeploy + ALB for zero-downtime.
Tasks are ephemeral: store nothing local; use S3/DB for persistence.
Failure handling & retries
Synchronous HTTP errors: implement retries with exponential backoff at client or service edge; circuit breaker for downstream failures.
Asynchronous (SQS):
Message processing failures → retry until maxReceiveCount → DLQ.
Idempotency keys for handlers so reprocessing is safe.
Dead-letter queue inspection + automated alerting for DLQ growth.
Flyway/migrations: run in CI/CD or as a pre-deploy job; avoid running heavy migrations during scale events.
Observability & tracing
Centralized logs in CloudWatch (one group per task/service).
Metrics: CloudWatch for CPU/memory, ALB request count/latency, SQS queue length, custom business metrics.
Distributed tracing: X-Ray or OpenTelemetry for tracing request across microservices and queues. Correlate trace IDs in SNS/SQS messages.
Security & networking
Tasks run in private subnets (no public IP unless needed); ALB in public subnets.
Security Groups: ALB→ECS tasks, ECS tasks→RDS/Redis only via allowed SGs.
IAM least-privilege: task role grants only necessary access (SQS, SNS, SecretsManager).
Use VPC endpoints for services (S3, SecretsManager) to avoid public egress.
Practical scaling rules-of-thumb
Keep transaction requests short and synchronous; offload heavy work to async queues.
Use small connection pool per task (e.g., 5-10) and scale tasks to increase DB capacity rather than large pools per task.
For SQS consumers, scale by messages-per-second target (e.g., each consumer handles N msg/s).
Use health checks & graceful shutdown to avoid dropped requests during scale-down.
Example single request full flow (user signup)
Client POST /users → API Gateway → ALB → Users ECS task
UsersService @Transactional:
Save user to RDS via UserRepository (EntityManager/Hibernate uses pooled JDBC connection).
Publish "user.created" to SNS topic (transaction-events) or directly call EmailSender.
Return 201 Created to client.
SNS fans out to notification queue → Email worker (ECS consumer) pulls SQS message → sends email via SES → logs results; failures go to DLQ.
Summary (one-liner)

ECS TaskDefinition defines how containers run; ECS Service manages desired count and deployment; ALB routes traffic to task IPs; autoscaling adjusts task count using ALB/CW or queue length metrics; SNS/SQS decouple producers and consumers enabling resilient, scalable async processing.
If you want, I can:



