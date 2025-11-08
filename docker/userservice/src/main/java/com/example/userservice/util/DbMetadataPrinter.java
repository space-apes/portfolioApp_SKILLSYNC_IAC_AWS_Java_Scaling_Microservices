package com.example.userservice.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbMetadataPrinter {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: DbMetadataPrinter <jdbcUrl> <user> <password>");
            System.exit(2);
        }

        String url = args[0];
        String user = args[1];
        String pass = args[2];

        System.out.println("Connecting to: " + url + " as user " + user);

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            DatabaseMetaData md = conn.getMetaData();
            System.out.println("Database Product Name : " + md.getDatabaseProductName());
            System.out.println("Database Product Version : " + md.getDatabaseProductVersion());
            System.out.println("Driver Name : " + md.getDriverName());
            System.out.println("Driver Version : " + md.getDriverVersion());
            System.out.println("URL from metadata: " + md.getURL());
            System.out.println("UserName from metadata: " + md.getUserName());

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT 1")) {
                if (rs.next()) System.out.println("Simple query OK, result=" + rs.getInt(1));
            } catch (SQLException e) {
                System.err.println("Simple query failed: " + e);
            }

        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(3);
        }
    }
}
