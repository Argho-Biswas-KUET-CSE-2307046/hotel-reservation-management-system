package hotel.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {

        // SQL for Rooms table
        String createRoomsTable = """
                CREATE TABLE IF NOT EXISTS rooms (
                    room_number INTEGER PRIMARY KEY,
                    room_type TEXT NOT NULL,
                    price REAL NOT NULL,
                    available INTEGER NOT NULL
                )
                """;

        // SQL for Customers table
        String createCustomersTable = """
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    phone TEXT,
                    email TEXT,
                    address TEXT,
                    photo_path TEXT
                )
                """;

        // SQL for Bookings table
        String createBookingsTable = """
                CREATE TABLE IF NOT EXISTS bookings (
                    booking_id INTEGER PRIMARY KEY,
                    customer_id INTEGER NOT NULL,
                    room_number INTEGER NOT NULL,
                    check_in TEXT NOT NULL,
                    check_out TEXT NOT NULL,

                    FOREIGN KEY (customer_id)
                        REFERENCES customers(customer_id),

                    FOREIGN KEY (room_number)
                        REFERENCES rooms(room_number)
                )
                """;

        // SQL for Financial Transactions table
        String createFinancialTransactionsTable = """
                CREATE TABLE IF NOT EXISTS financial_transactions (
                    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    transaction_date TEXT NOT NULL,

                    income_amount REAL DEFAULT 0,
                    income_reason TEXT,

                    expense_amount REAL DEFAULT 0,
                    expense_reason TEXT,

                    booking_id INTEGER UNIQUE,

                    FOREIGN KEY (booking_id)
                        REFERENCES bookings(booking_id)
                )
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement =
                     connection.createStatement()) {

            // Enable foreign key support in SQLite
            statement.execute("PRAGMA foreign_keys = ON");

            // Create Rooms table
            statement.execute(createRoomsTable);

            // Create Customers table
            statement.execute(createCustomersTable);

            // Create Bookings table
            statement.execute(createBookingsTable);

            // Create Financial Transactions table
            statement.execute(createFinancialTransactionsTable);

            System.out.println(
                    "Database tables created successfully!"
            );

        } catch (Exception e) {

            System.out.println(
                    "Database initialization failed."
            );

            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        initializeDatabase();
    }
}