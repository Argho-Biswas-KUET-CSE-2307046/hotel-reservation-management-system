package hotel.controller;

import hotel.database.BookingDAO;
import hotel.model.Booking;

import javafx.beans.property.ReadOnlyObjectWrapper;
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

    private BookingDAO bookingDAO;


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
        // CREATE DATABASE ACCESS OBJECT
        // ---------------------------------------------------------

        try {

            bookingDAO = new BookingDAO();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not connect to the booking database."
            );

            return;
        }


        // ---------------------------------------------------------
        // BOOKING ID COLUMN
        // ---------------------------------------------------------

        bookingIdColumn.setCellValueFactory(
                cellData ->
                        new ReadOnlyObjectWrapper<>(
                                cellData.getValue().getBookingId()
                        )
        );


        // ---------------------------------------------------------
        // CUSTOMER COLUMN
        // ---------------------------------------------------------

        customerColumn.setCellValueFactory(
                cellData -> {

                    Booking booking =
                            cellData.getValue();

                    if (booking == null ||
                            booking.getCustomer() == null) {

                        return new ReadOnlyStringWrapper(
                                "N/A"
                        );
                    }

                    if (booking.getCustomer().getName() == null) {

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

        roomColumn.setCellValueFactory(
                cellData -> {

                    Booking booking =
                            cellData.getValue();

                    if (booking == null ||
                            booking.getRoom() == null) {

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

                    if (booking == null ||
                            booking.getCheckIn() == null) {

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

                    if (booking == null ||
                            booking.getCheckOut() == null) {

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
        // LOAD BOOKING HISTORY
        // ---------------------------------------------------------

        loadBookingHistory();
    }


    // =========================================================
    // LOAD BOOKING HISTORY FROM DATABASE
    // =========================================================

    private void loadBookingHistory() {

        if (bookingDAO == null) {

            return;
        }


        try {

            // -----------------------------------------------------
            // GET BOOKINGS FROM DATABASE
            // -----------------------------------------------------

            List<Booking> bookings =
                    bookingDAO.getAllBookings();


            // -----------------------------------------------------
            // CLEAR OLD DATA
            // -----------------------------------------------------

            bookingHistoryList.clear();


            // -----------------------------------------------------
            // ADD BOOKINGS
            // -----------------------------------------------------

            if (bookings != null) {

                bookingHistoryList.addAll(
                        bookings
                );
            }

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

        if (bookingHistoryTable == null ||
                bookingHistoryTable.getScene() == null) {

            return;
        }


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