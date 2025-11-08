package com.example.userservice.util;

import org.flywaydb.core.Flyway;

public class DbMigrator {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: DbMigrator <jdbcUrl> <user> <password>");
            System.exit(2);
        }

        String url = args[0];
        String user = args[1];
        String pass = args[2];

        System.out.println("Running Flyway migrate against: " + url + " as " + user);

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, pass)
                .locations("classpath:db/migration")
                .load();

        org.flywaydb.core.api.output.MigrateResult result = flyway.migrate();
        System.out.println("Migrations applied: " + result.migrationsExecuted);
    }
}
