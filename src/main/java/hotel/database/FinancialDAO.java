package hotel.database;

import hotel.model.FinancialTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialDAO {


    // =========================================================
    // ADD INCOME
    // =========================================================

    public boolean addIncome(
            LocalDate transactionDate,
            double incomeAmount,
            String incomeReason,
            int bookingId) {

        String sql = """
                INSERT INTO financial_transactions
                (
                    transaction_date,
                    income_amount,
                    income_reason,
                    expense_amount,
                    expense_reason,
                    booking_id
                )
                VALUES (?, ?, ?, 0, '', ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    transactionDate.toString()
            );

            statement.setDouble(
                    2,
                    incomeAmount
            );

            statement.setString(
                    3,
                    incomeReason
            );

            statement.setInt(
                    4,
                    bookingId
            );

            statement.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // ADD EXPENSE
    // =========================================================

    public boolean addExpense(
            LocalDate transactionDate,
            double expenseAmount,
            String expenseReason) {

        String sql = """
                INSERT INTO financial_transactions
                (
                    transaction_date,
                    income_amount,
                    income_reason,
                    expense_amount,
                    expense_reason,
                    booking_id
                )
                VALUES (?, 0, '', ?, ?, NULL)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    transactionDate.toString()
            );

            statement.setDouble(
                    2,
                    expenseAmount
            );

            statement.setString(
                    3,
                    expenseReason
            );

            statement.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // GET ALL FINANCIAL TRANSACTIONS
    // =========================================================

    public List<FinancialTransaction> getAllTransactions() {

        List<FinancialTransaction> transactions =
                new ArrayList<>();

        String sql = """
                SELECT
                    transaction_id,
                    transaction_date,
                    income_amount,
                    income_reason,
                    expense_amount,
                    expense_reason,
                    booking_id

                FROM financial_transactions

                ORDER BY transaction_date DESC,
                         transaction_id DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                FinancialTransaction transaction =
                        createTransactionFromResultSet(
                                resultSet
                        );

                transactions.add(transaction);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return transactions;
    }


    // =========================================================
    // GET LAST 30 DAYS TRANSACTIONS
    // =========================================================

    public List<FinancialTransaction>
    getLast30DaysTransactions() {

        LocalDate today =
                LocalDate.now();

        LocalDate startDate =
                today.minusDays(29);

        return getTransactionsBetweenDates(
                startDate,
                today
        );
    }


    // =========================================================
    // GET TRANSACTIONS BETWEEN TWO DATES
    // =========================================================

    public List<FinancialTransaction>
    getTransactionsBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {

        List<FinancialTransaction> transactions =
                new ArrayList<>();

        String sql = """
                SELECT
                    transaction_id,
                    transaction_date,
                    income_amount,
                    income_reason,
                    expense_amount,
                    expense_reason,
                    booking_id

                FROM financial_transactions

                WHERE transaction_date >= ?
                  AND transaction_date <= ?

                ORDER BY transaction_date DESC,
                         transaction_id DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    startDate.toString()
            );

            statement.setString(
                    2,
                    endDate.toString()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    FinancialTransaction transaction =
                            createTransactionFromResultSet(
                                    resultSet
                            );

                    transactions.add(transaction);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return transactions;
    }


    // =========================================================
    // CALCULATE TOTAL INCOME
    // =========================================================

    public double getTotalIncome() {

        String sql = """
                SELECT
                    COALESCE(
                        SUM(income_amount),
                        0
                    )

                FROM financial_transactions
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                return resultSet.getDouble(1);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0.0;
    }


    // =========================================================
    // CALCULATE TOTAL EXPENSE
    // =========================================================

    public double getTotalExpense() {

        String sql = """
                SELECT
                    COALESCE(
                        SUM(expense_amount),
                        0
                    )

                FROM financial_transactions
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                return resultSet.getDouble(1);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0.0;
    }


    // =========================================================
    // TOTAL INCOME FOR DATE RANGE
    // =========================================================

    public double getTotalIncome(
            LocalDate startDate,
            LocalDate endDate) {

        String sql = """
                SELECT
                    COALESCE(
                        SUM(income_amount),
                        0
                    )

                FROM financial_transactions

                WHERE transaction_date >= ?
                  AND transaction_date <= ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    startDate.toString()
            );

            statement.setString(
                    2,
                    endDate.toString()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return resultSet.getDouble(1);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0.0;
    }


    // =========================================================
    // TOTAL EXPENSE FOR DATE RANGE
    // =========================================================

    public double getTotalExpense(
            LocalDate startDate,
            LocalDate endDate) {

        String sql = """
                SELECT
                    COALESCE(
                        SUM(expense_amount),
                        0
                    )

                FROM financial_transactions

                WHERE transaction_date >= ?
                  AND transaction_date <= ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    startDate.toString()
            );

            statement.setString(
                    2,
                    endDate.toString()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return resultSet.getDouble(1);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0.0;
    }


    // =========================================================
    // CHECK WHETHER BOOKING INCOME ALREADY EXISTS
    // =========================================================

    public boolean incomeExistsForBooking(
            int bookingId) {

        String sql = """
                SELECT
                    COUNT(*)

                FROM financial_transactions

                WHERE booking_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    bookingId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }


    // =========================================================
    // DELETE INCOME FOR A BOOKING
    // =========================================================

    public boolean deleteIncomeForBooking(
            int bookingId) {

        String sql = """
                DELETE FROM financial_transactions
                WHERE booking_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    bookingId
            );

            statement.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // CREATE OBJECT FROM RESULT SET
    // =========================================================

    private FinancialTransaction
    createTransactionFromResultSet(
            ResultSet resultSet)
            throws Exception {

        int transactionId =
                resultSet.getInt(
                        "transaction_id"
                );

        LocalDate transactionDate =
                LocalDate.parse(
                        resultSet.getString(
                                "transaction_date"
                        )
                );

        double incomeAmount =
                resultSet.getDouble(
                        "income_amount"
                );

        String incomeReason =
                resultSet.getString(
                        "income_reason"
                );

        double expenseAmount =
                resultSet.getDouble(
                        "expense_amount"
                );

        String expenseReason =
                resultSet.getString(
                        "expense_reason"
                );

        int bookingIdValue =
                resultSet.getInt(
                        "booking_id"
                );

        Integer bookingId;

        if (resultSet.wasNull()) {

            bookingId = null;

        } else {

            bookingId = bookingIdValue;
        }


        return new FinancialTransaction(
                transactionId,
                transactionDate,
                incomeAmount,
                incomeReason,
                expenseAmount,
                expenseReason,
                bookingId
        );
    }
}