package hotel.model;

import java.time.LocalDate;

public class FinancialTransaction {

    private int transactionId;
    private LocalDate transactionDate;

    private double incomeAmount;
    private String incomeReason;

    private double expenseAmount;
    private String expenseReason;

    private Integer bookingId;


    // Default Constructor
    public FinancialTransaction() {
    }


    // Full Constructor
    public FinancialTransaction(
            int transactionId,
            LocalDate transactionDate,
            double incomeAmount,
            String incomeReason,
            double expenseAmount,
            String expenseReason,
            Integer bookingId) {

        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.incomeAmount = incomeAmount;
        this.incomeReason = incomeReason;
        this.expenseAmount = expenseAmount;
        this.expenseReason = expenseReason;
        this.bookingId = bookingId;
    }


    // Constructor for Income
    public FinancialTransaction(
            LocalDate transactionDate,
            double incomeAmount,
            String incomeReason,
            Integer bookingId) {

        this.transactionDate = transactionDate;
        this.incomeAmount = incomeAmount;
        this.incomeReason = incomeReason;
        this.expenseAmount = 0.0;
        this.expenseReason = "";
        this.bookingId = bookingId;
    }


    // Constructor for Expense
    public FinancialTransaction(
            LocalDate transactionDate,
            double expenseAmount,
            String expenseReason) {

        this.transactionDate = transactionDate;
        this.incomeAmount = 0.0;
        this.incomeReason = "";
        this.expenseAmount = expenseAmount;
        this.expenseReason = expenseReason;
        this.bookingId = null;
    }


    // Get Transaction ID
    public int getTransactionId() {
        return transactionId;
    }

    // Set Transaction ID
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }


    // Get Transaction Date
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    // Set Transaction Date
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }


    // Get Income Amount
    public double getIncomeAmount() {
        return incomeAmount;
    }

    // Set Income Amount
    public void setIncomeAmount(double incomeAmount) {
        this.incomeAmount = incomeAmount;
    }


    // Get Income Reason
    public String getIncomeReason() {
        return incomeReason;
    }

    // Set Income Reason
    public void setIncomeReason(String incomeReason) {
        this.incomeReason = incomeReason;
    }


    // Get Expense Amount
    public double getExpenseAmount() {
        return expenseAmount;
    }

    // Set Expense Amount
    public void setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }


    // Get Expense Reason
    public String getExpenseReason() {
        return expenseReason;
    }

    // Set Expense Reason
    public void setExpenseReason(String expenseReason) {
        this.expenseReason = expenseReason;
    }


    // Get Booking ID
    public Integer getBookingId() {
        return bookingId;
    }

    // Set Booking ID
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }


    // Calculate Balance
    public double getBalance() {
        return incomeAmount - expenseAmount;
    }


    @Override
    public String toString() {
        return "FinancialTransaction{" +
                "transactionId=" + transactionId +
                ", transactionDate=" + transactionDate +
                ", incomeAmount=" + incomeAmount +
                ", incomeReason='" + incomeReason + '\'' +
                ", expenseAmount=" + expenseAmount +
                ", expenseReason='" + expenseReason + '\'' +
                ", bookingId=" + bookingId +
                '}';
    }
}