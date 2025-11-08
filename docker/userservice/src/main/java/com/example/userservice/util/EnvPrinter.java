package com.example.userservice.util;

public class EnvPrinter {
    public static void main(String[] args) {
        String env = System.getenv("SPRING_DATASOURCE_URL");
        String prop = System.getProperty("spring.datasource.url");
        System.out.println("ENV SPRING_DATASOURCE_URL: '" + env + "'");
        System.out.println("SYS_PROP spring.datasource.url: '" + prop + "'");
        // print length and hex of the env value if present
        if (env != null) {
            System.out.println("env.length=" + env.length());
            StringBuilder hex = new StringBuilder();
            for (byte b : env.getBytes()) {
                hex.append(String.format(" %02x", b));
            }
            System.out.println("env.hex:" + hex.toString());
        }
    }
}
