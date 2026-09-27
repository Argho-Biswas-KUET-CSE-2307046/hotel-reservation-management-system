package hotel.controller;

import hotel.database.BookingDAO;
import hotel.model.Booking;

import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.stage.Stage;

import java.util.List;


// =========================================================
// BOOKING HISTORY CONTROLLER
// =========================================================

public class BookingHistoryController {

    // =========================================================
    // DATABASE
    // =========================================================

    private final BookingDAO bookingDAO =
            new BookingDAO();


    // =========================================================
    // TABLE
    // =========================================================

    @FXML
    private TableView<Booking> bookingHistoryTable;


    // =========================================================
    // TABLE COLUMNS
    // =========================================================

    @FXML
    private TableColumn<Booking, Integer> bookingIdColumn;

    @FXML
    private TableColumn<Booking, String> customerColumn;

    @FXML
    private TableColumn<Booking, String> roomColumn;

    @FXML
    private TableColumn<Booking, String> checkInColumn;

    @FXML
    private TableColumn<Booking, String> checkOutColumn;


    // =========================================================
    // BOOKING LIST
    // =========================================================

    private final ObservableList<Booking> bookingHistoryList =
            FXCollections.observableArrayList();


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        // ---------------------------------------------------------
        // BOOKING ID COLUMN
        // ---------------------------------------------------------

        bookingIdColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.ReadOnlyObjectWrapper<>(
                                cellData.getValue().getBookingId()
                        )
        );


        // ---------------------------------------------------------
        // CUSTOMER COLUMN
        // ---------------------------------------------------------
        // Display only the customer's name.
        // Instead of:
        // Customer{customerId=1, name='Argho', ...}
        //
        // It will display:
        // Argho
        // ---------------------------------------------------------

        customerColumn.setCellValueFactory(
                cellData -> {

                    Booking booking =
                            cellData.getValue();

                    if (booking.getCustomer() == null) {

                        return new ReadOnlyStringWrapper(
                                "N/A"
                        );
                    }

                    return new ReadOnlyStringWrapper(
                            booking.getCustomer().getName()
                    );
                }
        );


        // ---------------------------------------------------------
        // ROOM COLUMN
        // ---------------------------------------------------------
        // Display room number and room type.
        //
        // Example:
        // 101 - Deluxe
        // ---------------------------------------------------------

        roomColumn.setCellValueFactory(
                cellData -> {

                    Booking booking =
                            cellData.getValue();

                    if (booking.getRoom() == null) {

                        return new ReadOnlyStringWrapper(
                                "N/A"
                        );
                    }

                    return new ReadOnlyStringWrapper(
                            booking.getRoom().getRoomNumber()
                                    + " - "
                                    + booking.getRoom().getRoomType()
                    );
                }
        );


        // ---------------------------------------------------------
        // CHECK-IN COLUMN
        // ---------------------------------------------------------

        checkInColumn.setCellValueFactory(
                cellData -> {

                    Booking booking =
                            cellData.getValue();

                    if (booking.getCheckIn() == null) {

                        return new ReadOnlyStringWrapper(
                                "N/A"
                        );
                    }

                    return new ReadOnlyStringWrapper(
                            booking.getCheckIn().toString()
                    );
                }
        );


        // ---------------------------------------------------------
        // CHECK-OUT COLUMN
        // ---------------------------------------------------------

        checkOutColumn.setCellValueFactory(
                cellData -> {

                    Booking booking =
                            cellData.getValue();

                    if (booking.getCheckOut() == null) {

                        return new ReadOnlyStringWrapper(
                                "N/A"
                        );
                    }

                    return new ReadOnlyStringWrapper(
                            booking.getCheckOut().toString()
                    );
                }
        );


        // ---------------------------------------------------------
        // SET TABLE ITEMS
        // ---------------------------------------------------------

        bookingHistoryTable.setItems(
                bookingHistoryList
        );


        // ---------------------------------------------------------
        // LOAD BOOKINGS FROM SQLITE
        // ---------------------------------------------------------

        loadBookingHistory();
    }


    // =========================================================
    // LOAD BOOKING HISTORY FROM DATABASE
    // =========================================================

    private void loadBookingHistory() {

        try {

            // ---------------------------------------------------------
            // GET BOOKINGS FROM SQLITE
            // ---------------------------------------------------------

            List<Booking> bookings =
                    bookingDAO.getAllBookings();


            // ---------------------------------------------------------
            // CLEAR OLD DATA
            // ---------------------------------------------------------

            bookingHistoryList.clear();


            // ---------------------------------------------------------
            // ADD DATABASE BOOKINGS
            // ---------------------------------------------------------

            bookingHistoryList.addAll(
                    bookings
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not load booking history from database."
            );
        }
    }


    // =========================================================
    // REFRESH BOOKING HISTORY
    // =========================================================

    @FXML
    private void refreshBookingHistory() {

        loadBookingHistory();
    }


    // =========================================================
    // BACK TO MAIN
    // =========================================================

    @FXML
    private void backToMain() {

        Stage stage =
                (Stage) bookingHistoryTable
                        .getScene()
                        .getWindow();

        stage.close();
    }


    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private void showError(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}