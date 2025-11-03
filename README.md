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
  - controller layer (http requests)
  - service layer (business logic) 
  - repository layer (database operations)
  - entity/model layer (JPA entities mapped to database tables) 
  - (not a layer) have exception handling done with global @ControllerAdvice annotation
4.  add another service with its own db docker instances: TransactionService. have service to service communication via REST (creating transaction from userIds requires looking up first name and last name from UserService). add unit tests and endpoint tests at this stage.  
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

