# userservice — local development

TODO: 
- entity business logic tests
- endpoint tests
- exception handling for endpoints



This module runs a small Spring Boot service (`userservice`). These notes show how to run it locally against a MySQL database using the `dev` profile.

## Prerequisites
- Java 17+ (project targets 17)
- Maven (the repo includes `mvnw` so you don't need a system Maven)
- A running MySQL instance accessible to your machine

## Environment variables
The `dev` profile reads database credentials from environment variables. These must be set before starting the app:

- `SPRING_DATASOURCE_URL` — JDBC URL, e.g. `jdbc:mysql://localhost:3310/users_db`
- `SPRING_DATASOURCE_USERNAME` — DB username (e.g. `root`)
- `SPRING_DATASOURCE_PASSWORD` — DB password

For convenience you can create a local `.env` file (example below) and source it before running. The repository includes a `.env` template that is gitignored to avoid leaking secrets.

Create a local `.env` file (DO NOT commit this file) containing the three environment variables listed above.

## Run the service locally
From the `docker/userservice` directory:

1. Load your env file (if using `.env`):

```bash
source .env
```

2. Start the app with the `dev` profile (Flyway will run on startup):

```bash
./mvnw -Dspring-boot.run.profiles=dev -DskipTests spring-boot:run
```

Or use the included helper script:

```bash
bash run-dev.sh
# or make it executable and run directly
chmod +x run-dev.sh
./run-dev.sh
```

### Notes about env propagation and alternatives

- The `.env` file defines the variables in the current shell. To ensure child processes (like the Maven wrapper and the JVM it starts) receive the variables, export them before running Maven. The easiest ways:

	- Use the script which exports `.env` for you (the `run-dev.sh` included in this module uses `set -a; source .env; set +a` to export values).
	- Or export manually in your shell:

		```bash
		set -a; source .env; set +a
		./mvnw -Dspring-boot.run.profiles=dev -DskipTests spring-boot:run
		```

- Alternatively, pass Flyway/maven properties directly on the command line when running Flyway goals:

		```bash
		set -a; source .env; set +a
		./mvnw -DskipTests \
			-Dflyway.url="$SPRING_DATASOURCE_URL" \
			-Dflyway.user="$SPRING_DATASOURCE_USERNAME" \
			-Dflyway.password="$SPRING_DATASOURCE_PASSWORD" \
			flyway:info
		```

- If Flyway fails with duplicate-version errors, run a clean build first to remove stale files in `target/classes`:

		```bash
		./mvnw -DskipTests clean package
		```

	Then run `flyway:info` or `flyway:migrate` as above.


## Run Flyway migrations manually
If you prefer to run migrations separately (CI or manual step):

```bash
source .env
./mvnw -DskipTests flyway:migrate
```

## Notes
- The `application-dev.properties` file requires the three env vars and contains no embedded credentials. The local `.env` file is included in `.gitignore`.
- If you ever accidentally commit secrets, rotate them immediately and consider history rewrite — contact me if you want help with that.
