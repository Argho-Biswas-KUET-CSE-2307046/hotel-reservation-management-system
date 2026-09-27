package hotel.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseVerification {

    public static void main(String[] args) {

        String sql = """
                SELECT
                    name
                FROM sqlite_master
                WHERE type = 'table'
                AND name NOT LIKE 'sqlite_%'
                ORDER BY name
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(sql)
        ) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("       DATABASE VERIFICATION");
            System.out.println("=================================");
            System.out.println();

            System.out.println("SQLite Tables:");

            boolean financialTableFound = false;

            while (resultSet.next()) {

                String tableName =
                        resultSet.getString("name");

                System.out.println("- " + tableName);

                if (tableName.equals(
                        "financial_transactions")) {

                    financialTableFound = true;
                }
            }

            System.out.println();

            // -----------------------------------------------------
            // CHECK FINANCIAL TABLE
            // -----------------------------------------------------

            if (!financialTableFound) {

                System.out.println(
                        "ERROR: financial_transactions table NOT FOUND!"
                );

            } else {

                System.out.println(
                        "financial_transactions table FOUND."
                );

                System.out.println();
                System.out.println(
                        "Financial table columns:"
                );

                String columnSQL =
                        "PRAGMA table_info(financial_transactions)";

                try (
                        Statement columnStatement =
                                connection.createStatement();

                        ResultSet columns =
                                columnStatement.executeQuery(
                                        columnSQL
                                )
                ) {

                    while (columns.next()) {

                        int columnNumber =
                                columns.getInt("cid");

                        String columnName =
                                columns.getString("name");

                        String columnType =
                                columns.getString("type");

                        String notNull =
                                columns.getString("notnull");

                        String defaultValue =
                                columns.getString("dflt_value");

                        String primaryKey =
                                columns.getString("pk");

                        System.out.println(
                                columnNumber
                                        + " | "
                                        + columnName
                                        + " | "
                                        + columnType
                                        + " | NOT NULL="
                                        + notNull
                                        + " | DEFAULT="
                                        + defaultValue
                                        + " | PK="
                                        + primaryKey
                        );
                    }
                }
            }

            System.out.println();
            System.out.println("=================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "Could not verify database."
            );

            e.printStackTrace();
        }
    }
}