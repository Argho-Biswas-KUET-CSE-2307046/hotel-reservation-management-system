package hotel.controller;

import hotel.database.FinancialDAO;
import hotel.model.FinancialTransaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;


/**
 * Controller for Finance & Reports page.
 *
 * Handles:
 * 1. Expense management
 * 2. Financial transaction table
 * 3. Last 30 days summary
 * 4. Date filtering
 * 5. Income filtering
 * 6. Expense filtering
 * 7. Income vs Expense chart
 */
public class FinanceController {


    // =========================================================
    // EXPENSE SECTION
    // =========================================================

    @FXML
    private DatePicker expenseDatePicker;

    @FXML
    private TextField expenseAmountField;

    @FXML
    private TextArea expenseReasonArea;


    // =========================================================
    // LAST 30 DAYS SUMMARY
    // =========================================================

    @FXML
    private Label last30IncomeLabel;

    @FXML
    private Label last30ExpenseLabel;

    @FXML
    private Label last30BalanceLabel;


    // =========================================================
    // FILTER SECTION
    // =========================================================

    @FXML
    private DatePicker minDatePicker;

    @FXML
    private DatePicker maxDatePicker;

    @FXML
    private TextField minIncomeField;

    @FXML
    private TextField maxIncomeField;

    @FXML
    private TextField minExpenseField;

    @FXML
    private TextField maxExpenseField;


    // =========================================================
    // TABLE
    // =========================================================

    @FXML
    private TableView<FinancialTransaction> financialTable;

    @FXML
    private TableColumn<FinancialTransaction, LocalDate> dateColumn;

    @FXML
    private TableColumn<FinancialTransaction, Double> incomeColumn;

    @FXML
    private TableColumn<FinancialTransaction, String> incomeReasonColumn;

    @FXML
    private TableColumn<FinancialTransaction, Double> expenseColumn;

    @FXML
    private TableColumn<FinancialTransaction, String> expenseReasonColumn;


    // =========================================================
    // SELECTED RANGE TOTALS
    // =========================================================

    @FXML
    private Label filteredIncomeLabel;

    @FXML
    private Label filteredExpenseLabel;

    @FXML
    private Label filteredBalanceLabel;


    // =========================================================
    // CHART
    // =========================================================

    @FXML
    private BarChart<String, Number> financialChart;


    // =========================================================
    // DAO
    // =========================================================

    private final FinancialDAO financialDAO =
            new FinancialDAO();


    // =========================================================
    // DATA LIST
    // =========================================================

    private final ObservableList<FinancialTransaction>
            transactionList =
            FXCollections.observableArrayList();


    // =========================================================
    // DATE FORMATTER
    // =========================================================

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(
                    "dd-MM-yyyy"
            );


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    private void initialize() {

        setupTable();

        setupDatePickers();

        loadFinancialData();

        updateLast30DaysSummary();

        updateFilteredTotals(
                transactionList
        );

        updateChart(
                transactionList
        );
    }


    // =========================================================
    // SETUP TABLE
    // =========================================================

    private void setupTable() {

        // ---------------------------------------------------------
        // DATE
        // ---------------------------------------------------------

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "transactionDate"
                )
        );


        // ---------------------------------------------------------
        // INCOME
        // ---------------------------------------------------------

        incomeColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "incomeAmount"
                )
        );


        // ---------------------------------------------------------
        // INCOME REASON
        // ---------------------------------------------------------

        incomeReasonColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "incomeReason"
                )
        );


        // ---------------------------------------------------------
        // EXPENSE
        // ---------------------------------------------------------

        expenseColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "expenseAmount"
                )
        );


        // ---------------------------------------------------------
        // EXPENSE REASON
        // ---------------------------------------------------------

        expenseReasonColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "expenseReason"
                )
        );


        // ---------------------------------------------------------
        // SET DATA
        // ---------------------------------------------------------

        financialTable.setItems(
                transactionList
        );


        // ---------------------------------------------------------
        // DATE FORMAT
        // ---------------------------------------------------------

        dateColumn.setCellFactory(
                column -> new javafx.scene.control.TableCell<>() {

                    @Override
                    protected void updateItem(
                            LocalDate date,
                            boolean empty) {

                        super.updateItem(
                                date,
                                empty
                        );

                        if (empty || date == null) {

                            setText(null);

                        } else {

                            setText(
                                    date.format(
                                            dateFormatter
                                    )
                            );
                        }
                    }
                }
        );


        // ---------------------------------------------------------
        // INCOME CURRENCY FORMAT
        // ---------------------------------------------------------

        incomeColumn.setCellFactory(
                column -> new javafx.scene.control.TableCell<>() {

                    @Override
                    protected void updateItem(
                            Double amount,
                            boolean empty) {

                        super.updateItem(
                                amount,
                                empty
                        );

                        if (empty || amount == null) {

                            setText(null);

                        } else {

                            setText(
                                    String.format(
                                            Locale.US,
                                            "৳ %.2f",
                                            amount
                                    )
                            );
                        }
                    }
                }
        );


        // ---------------------------------------------------------
        // EXPENSE CURRENCY FORMAT
        // ---------------------------------------------------------

        expenseColumn.setCellFactory(
                column -> new javafx.scene.control.TableCell<>() {

                    @Override
                    protected void updateItem(
                            Double amount,
                            boolean empty) {

                        super.updateItem(
                                amount,
                                empty
                        );

                        if (empty || amount == null) {

                            setText(null);

                        } else {

                            setText(
                                    String.format(
                                            Locale.US,
                                            "৳ %.2f",
                                            amount
                                    )
                            );
                        }
                    }
                }
        );
    }


    // =========================================================
    // DATE PICKER SETUP
    // =========================================================

    private void setupDatePickers() {

        expenseDatePicker.setValue(
                LocalDate.now()
        );
    }


    // =========================================================
    // LOAD FINANCIAL DATA
    // =========================================================

    private void loadFinancialData() {

        try {

            List<FinancialTransaction> transactions =
                    financialDAO.getAllTransactions();


            transactionList.setAll(
                    transactions
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not load financial transactions."
            );
        }
    }


    // =========================================================
    // ADD EXPENSE
    // =========================================================

    @FXML
    private void saveExpense() {

        // ---------------------------------------------------------
        // DATE
        // ---------------------------------------------------------

        LocalDate date =
                expenseDatePicker.getValue();

        if (date == null) {

            showWarning(
                    "Invalid Date",
                    "Please select an expense date."
            );

            return;
        }


        // ---------------------------------------------------------
        // AMOUNT
        // ---------------------------------------------------------

        String amountText =
                expenseAmountField
                        .getText()
                        .trim();


        if (amountText.isEmpty()) {

            showWarning(
                    "Invalid Amount",
                    "Please enter an expense amount."
            );

            return;
        }


        double amount;

        try {

            amount =
                    Double.parseDouble(
                            amountText
                    );

        } catch (NumberFormatException e) {

            showWarning(
                    "Invalid Amount",
                    "Please enter a valid numeric amount."
            );

            return;
        }


        // ---------------------------------------------------------
        // POSITIVE AMOUNT
        // ---------------------------------------------------------

        if (amount <= 0) {

            showWarning(
                    "Invalid Amount",
                    "Expense amount must be greater than zero."
            );

            return;
        }


        // ---------------------------------------------------------
        // REASON
        // ---------------------------------------------------------

        String reason =
                expenseReasonArea
                        .getText()
                        .trim();


        if (reason.isEmpty()) {

            showWarning(
                    "Missing Reason",
                    "Please enter the reason for the expense."
            );

            return;
        }


        // ---------------------------------------------------------
        // SAVE TO DATABASE
        // ---------------------------------------------------------

        try {

            boolean saved =
                    financialDAO.addExpense(
                            date,
                            amount,
                            reason
                    );

            if (!saved) {

                showError(
                        "Database Error",
                        "Expense could not be saved."
                );

                return;
            }


            // -----------------------------------------------------
            // SUCCESS
            // -----------------------------------------------------

            showMessage(
                    "Success",
                    "Expense saved successfully."
            );


            // -----------------------------------------------------
            // CLEAR FORM
            // -----------------------------------------------------

            expenseDatePicker.setValue(
                    LocalDate.now()
            );

            expenseAmountField.clear();

            expenseReasonArea.clear();


            // -----------------------------------------------------
            // REFRESH DATA
            // -----------------------------------------------------

            loadFinancialData();

            updateLast30DaysSummary();

            updateFilteredTotals(
                    transactionList
            );

            updateChart(
                    transactionList
            );


        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not save expense."
            );
        }
    }


    // =========================================================
    // LAST 30 DAYS SUMMARY
    // =========================================================

    private void updateLast30DaysSummary() {

        try {

            List<FinancialTransaction> transactions =
                    financialDAO
                            .getLast30DaysTransactions();


            double totalIncome = 0.0;

            double totalExpense = 0.0;


            for (FinancialTransaction transaction
                    : transactions) {

                totalIncome +=
                        transaction.getIncomeAmount();

                totalExpense +=
                        transaction.getExpenseAmount();
            }


            double balance =
                    totalIncome - totalExpense;


            // -----------------------------------------------------
            // DISPLAY
            // -----------------------------------------------------

            last30IncomeLabel.setText(
                    formatCurrency(
                            totalIncome
                    )
            );

            last30ExpenseLabel.setText(
                    formatCurrency(
                            totalExpense
                    )
            );

            last30BalanceLabel.setText(
                    formatCurrency(
                            balance
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not calculate last 30 days summary."
            );
        }
    }


    // =========================================================
    // APPLY FILTER
    // =========================================================

    @FXML
    private void applyFilter() {

        try {

            // -----------------------------------------------------
            // DATE VALUES
            // -----------------------------------------------------

            LocalDate minDate =
                    minDatePicker.getValue();

            LocalDate maxDate =
                    maxDatePicker.getValue();


            // -----------------------------------------------------
            // VALIDATE DATE RANGE
            // -----------------------------------------------------

            if (minDate != null
                    && maxDate != null
                    && minDate.isAfter(maxDate)) {

                showWarning(
                        "Invalid Date Range",
                        "Minimum date cannot be after maximum date."
                );

                return;
            }


            // -----------------------------------------------------
            // INCOME VALUES
            // -----------------------------------------------------

            Double minIncome =
                    parseOptionalAmount(
                            minIncomeField.getText()
                    );

            Double maxIncome =
                    parseOptionalAmount(
                            maxIncomeField.getText()
                    );


            // -----------------------------------------------------
            // EXPENSE VALUES
            // -----------------------------------------------------

            Double minExpense =
                    parseOptionalAmount(
                            minExpenseField.getText()
                    );

            Double maxExpense =
                    parseOptionalAmount(
                            maxExpenseField.getText()
                    );


            // -----------------------------------------------------
            // VALIDATE INCOME RANGE
            // -----------------------------------------------------

            if (minIncome != null
                    && maxIncome != null
                    && minIncome > maxIncome) {

                showWarning(
                        "Invalid Income Range",
                        "Minimum income cannot be greater than maximum income."
                );

                return;
            }


            // -----------------------------------------------------
            // VALIDATE EXPENSE RANGE
            // -----------------------------------------------------

            if (minExpense != null
                    && maxExpense != null
                    && minExpense > maxExpense) {

                showWarning(
                        "Invalid Expense Range",
                        "Minimum expense cannot be greater than maximum expense."
                );

                return;
            }


            // -----------------------------------------------------
            // APPLY ALL FILTERS
            // -----------------------------------------------------

            ObservableList<FinancialTransaction>
                    filteredList =
                    FXCollections.observableArrayList();


            for (FinancialTransaction transaction
                    : transactionList) {

                boolean matches = true;


                // -------------------------------------------------
                // DATE FILTER
                // -------------------------------------------------

                LocalDate transactionDate =
                        transaction.getTransactionDate();


                if (minDate != null
                        && transactionDate.isBefore(
                        minDate
                )) {

                    matches = false;
                }


                if (maxDate != null
                        && transactionDate.isAfter(
                        maxDate
                )) {

                    matches = false;
                }


                // -------------------------------------------------
                // INCOME FILTER
                // -------------------------------------------------

                double income =
                        transaction.getIncomeAmount();


                if (minIncome != null
                        && income < minIncome) {

                    matches = false;
                }


                if (maxIncome != null
                        && income > maxIncome) {

                    matches = false;
                }


                // -------------------------------------------------
                // EXPENSE FILTER
                // -------------------------------------------------

                double expense =
                        transaction.getExpenseAmount();


                if (minExpense != null
                        && expense < minExpense) {

                    matches = false;
                }


                if (maxExpense != null
                        && expense > maxExpense) {

                    matches = false;
                }


                // -------------------------------------------------
                // ADD IF ALL CONDITIONS MATCH
                // -------------------------------------------------

                if (matches) {

                    filteredList.add(
                            transaction
                    );
                }
            }


            // -----------------------------------------------------
            // UPDATE TABLE
            // -----------------------------------------------------

            financialTable.setItems(
                    filteredList
            );


            // -----------------------------------------------------
            // UPDATE TOTALS
            // -----------------------------------------------------

            updateFilteredTotals(
                    filteredList
            );


            // -----------------------------------------------------
            // UPDATE CHART
            // -----------------------------------------------------

            updateChart(
                    filteredList
            );


        } catch (NumberFormatException e) {

            showWarning(
                    "Invalid Amount",
                    "Please enter valid numbers in the amount filters."
            );
        }
    }


    // =========================================================
    // CLEAR FILTER
    // =========================================================

    @FXML
    private void clearFilter() {

        // ---------------------------------------------------------
        // CLEAR DATE
        // ---------------------------------------------------------

        minDatePicker.setValue(
                null
        );

        maxDatePicker.setValue(
                null
        );


        // ---------------------------------------------------------
        // CLEAR INCOME
        // ---------------------------------------------------------

        minIncomeField.clear();

        maxIncomeField.clear();


        // ---------------------------------------------------------
        // CLEAR EXPENSE
        // ---------------------------------------------------------

        minExpenseField.clear();

        maxExpenseField.clear();


        // ---------------------------------------------------------
        // RESTORE ALL DATA
        // ---------------------------------------------------------

        financialTable.setItems(
                transactionList
        );


        // ---------------------------------------------------------
        // UPDATE TOTALS
        // ---------------------------------------------------------

        updateFilteredTotals(
                transactionList
        );


        // ---------------------------------------------------------
        // UPDATE CHART
        // ---------------------------------------------------------

        updateChart(
                transactionList
        );
    }


    // =========================================================
    // SHOW LAST 30 DAYS
    // =========================================================

    @FXML
    private void showLast30Days() {

        LocalDate today =
                LocalDate.now();

        LocalDate startDate =
                today.minusDays(29);


        ObservableList<FinancialTransaction>
                last30Days =
                FXCollections.observableArrayList();


        for (FinancialTransaction transaction
                : transactionList) {

            LocalDate date =
                    transaction.getTransactionDate();


            if (!date.isBefore(startDate)
                    && !date.isAfter(today)) {

                last30Days.add(
                        transaction
                );
            }
        }


        // ---------------------------------------------------------
        // UPDATE TABLE
        // ---------------------------------------------------------

        financialTable.setItems(
                last30Days
        );


        // ---------------------------------------------------------
        // UPDATE TOTALS
        // ---------------------------------------------------------

        updateFilteredTotals(
                last30Days
        );


        // ---------------------------------------------------------
        // UPDATE CHART
        // ---------------------------------------------------------

        updateChart(
                last30Days
        );
    }


    // =========================================================
    // UPDATE FILTERED TOTALS
    // =========================================================

    private void updateFilteredTotals(
            List<FinancialTransaction> transactions) {

        double totalIncome = 0.0;

        double totalExpense = 0.0;


        for (FinancialTransaction transaction
                : transactions) {

            totalIncome +=
                    transaction.getIncomeAmount();

            totalExpense +=
                    transaction.getExpenseAmount();
        }


        double balance =
                totalIncome - totalExpense;


        filteredIncomeLabel.setText(
                formatCurrency(
                        totalIncome
                )
        );


        filteredExpenseLabel.setText(
                formatCurrency(
                        totalExpense
                )
        );


        filteredBalanceLabel.setText(
                formatCurrency(
                        balance
                )
        );
    }


    // =========================================================
    // UPDATE CHART
    // =========================================================

    private void updateChart(
            List<FinancialTransaction> transactions) {

        financialChart.getData().clear();


        XYChart.Series<String, Number>
                incomeSeries =
                new XYChart.Series<>();

        incomeSeries.setName(
                "Income"
        );


        XYChart.Series<String, Number>
                expenseSeries =
                new XYChart.Series<>();

        expenseSeries.setName(
                "Expense"
        );


        // ---------------------------------------------------------
        // GROUP BY DATE
        // ---------------------------------------------------------

        java.util.Map<LocalDate, Double>
                incomeByDate =
                new java.util.TreeMap<>();

        java.util.Map<LocalDate, Double>
                expenseByDate =
                new java.util.TreeMap<>();


        for (FinancialTransaction transaction
                : transactions) {

            LocalDate date =
                    transaction.getTransactionDate();


            incomeByDate.put(
                    date,
                    incomeByDate.getOrDefault(
                            date,
                            0.0
                    )
                            + transaction.getIncomeAmount()
            );


            expenseByDate.put(
                    date,
                    expenseByDate.getOrDefault(
                            date,
                            0.0
                    )
                            + transaction.getExpenseAmount()
            );
        }


        // ---------------------------------------------------------
        // CREATE CHART DATA
        // ---------------------------------------------------------

        java.util.Set<LocalDate> dates =
                new java.util.TreeSet<>();

        dates.addAll(
                incomeByDate.keySet()
        );

        dates.addAll(
                expenseByDate.keySet()
        );


        for (LocalDate date : dates) {

            String displayDate =
                    date.format(
                            dateFormatter
                    );


            incomeSeries.getData().add(
                    new XYChart.Data<>(
                            displayDate,
                            incomeByDate.getOrDefault(
                                    date,
                                    0.0
                            )
                    )
            );


            expenseSeries.getData().add(
                    new XYChart.Data<>(
                            displayDate,
                            expenseByDate.getOrDefault(
                                    date,
                                    0.0
                            )
                    )
            );
        }


        // ---------------------------------------------------------
        // ADD SERIES
        // ---------------------------------------------------------

        financialChart.getData().add(
                incomeSeries
        );

        financialChart.getData().add(
                expenseSeries
        );
    }


    // =========================================================
    // PARSE OPTIONAL AMOUNT
    // =========================================================

    private Double parseOptionalAmount(
            String text) {

        if (text == null
                || text.trim().isEmpty()) {

            return null;
        }


        double value =
                Double.parseDouble(
                        text.trim()
                );


        if (value < 0) {

            throw new NumberFormatException(
                    "Negative amount"
            );
        }


        return value;
    }


    // =========================================================
    // CURRENCY FORMAT
    // =========================================================

    private String formatCurrency(
            double amount) {

        return String.format(
                Locale.US,
                "৳ %.2f",
                amount
        );
    }


    // =========================================================
    // WARNING ALERT
    // =========================================================

    private void showWarning(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }


    // =========================================================
    // ERROR ALERT
    // =========================================================

    private void showError(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }


    // =========================================================
    // INFORMATION ALERT
    // =========================================================

    private void showMessage(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }


    // =========================================================
    // CLOSE FINANCE WINDOW
    // =========================================================

    @FXML
    private void closeFinanceWindow() {

        if (financialTable != null
                && financialTable.getScene() != null) {

            Stage stage =
                    (Stage) financialTable
                            .getScene()
                            .getWindow();

            stage.close();
        }
    }
}