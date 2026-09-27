package hotel.controller;

import hotel.database.BookingDAO;
import hotel.database.CustomerDAO;
import hotel.database.RoomDAO;
import hotel.database.FinancialDAO;

import hotel.model.Booking;
import hotel.model.BookingData;
import hotel.model.Customer;
import hotel.model.Room;

import hotel.service.BookingService;

import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;


// =========================================================
// BOOKING CONTROLLER
// =========================================================

public class BookingController {


    // =========================================================
    // SERVICES AND DATABASE
    // =========================================================

    private final BookingService bookingService =
            new BookingService();

    private final BookingDAO bookingDAO =
            new BookingDAO();

    private final CustomerDAO customerDAO =
            new CustomerDAO();

    private final RoomDAO roomDAO =
            new RoomDAO();


    // =========================================================
    // FINANCIAL DAO
    // =========================================================

    private final FinancialDAO financialDAO =
            new FinancialDAO();


    // =========================================================
    // TABLE AND COLUMNS
    // =========================================================

    @FXML
    private TableView<Booking> bookingTable;

    @FXML
    private TableColumn<Booking, Integer> bookingIdColumn;

    @FXML
    private TableColumn<Booking, Customer> customerColumn;

    @FXML
    private TableColumn<Booking, Room> roomColumn;

    @FXML
    private TableColumn<Booking, LocalDate> checkInColumn;

    @FXML
    private TableColumn<Booking, LocalDate> checkOutColumn;


    // =========================================================
    // BOOKING INPUT CONTROLS
    // =========================================================

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private ComboBox<Room> roomComboBox;

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;


    // =========================================================
    // BOOKING TYPE
    // =========================================================

    @FXML
    private RadioButton regularRadioButton;

    @FXML
    private RadioButton vipRadioButton;


    // =========================================================
    // EXTRA SERVICES
    // =========================================================

    @FXML
    private CheckBox breakfastCheckBox;

    @FXML
    private CheckBox airportPickupCheckBox;


    // =========================================================
    // PART 11 CONTROLS
    // =========================================================

    @FXML
    private ProgressBar bookingProgressBar;

    @FXML
    private Slider roomPriceSlider;

    @FXML
    private Label roomPriceLabel;

    @FXML
    private Spinner<Integer> guestSpinner;


    // =========================================================
    // CUSTOMER DISPLAY
    // =========================================================

    @FXML
    private Label selectedCustomerLabel;


    // =========================================================
    // LISTS
    // =========================================================

    private final ObservableList<Customer> customerList =
            FXCollections.observableArrayList();

    private final ObservableList<Room> roomList =
            FXCollections.observableArrayList();


    // =========================================================
    // SELECTED CUSTOMER
    // =========================================================

    private Customer selectedCustomer;


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        // ---------------------------------------------------------
        // TABLE COLUMN CONNECTIONS
        // ---------------------------------------------------------

        bookingIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("bookingId")
        );

        customerColumn.setCellValueFactory(
                new PropertyValueFactory<>("customer")
        );

        roomColumn.setCellValueFactory(
                new PropertyValueFactory<>("room")
        );

        checkInColumn.setCellValueFactory(
                new PropertyValueFactory<>("checkIn")
        );

        checkOutColumn.setCellValueFactory(
                new PropertyValueFactory<>("checkOut")
        );


        // ---------------------------------------------------------
        // SHARED BOOKING LIST
        // ---------------------------------------------------------

        bookingTable.setItems(
                BookingData.bookingList
        );


        // ---------------------------------------------------------
        // LOAD BOOKINGS FROM SQLITE
        // ---------------------------------------------------------

        loadBookingsFromDatabase();


        // ---------------------------------------------------------
        // LOAD CUSTOMERS DIRECTLY FROM SQLITE
        // ---------------------------------------------------------

        loadCustomersIntoComboBox();


        // ---------------------------------------------------------
        // LOAD ROOMS DIRECTLY FROM SQLITE
        // ---------------------------------------------------------

        loadRoomsIntoComboBox();


        // ---------------------------------------------------------
        // CUSTOMER COMBOBOX LISTENER
        // ---------------------------------------------------------

        if (customerComboBox != null) {

            customerComboBox.valueProperty()
                    .addListener(
                            (observable,
                             oldCustomer,
                             newCustomer) -> {

                                selectedCustomer =
                                        newCustomer;

                                updateSelectedCustomerLabel();

                                updateBookingProgress();
                            }
                    );
        }


        // ---------------------------------------------------------
        // ROOM COMBOBOX LISTENER
        // ---------------------------------------------------------

        if (roomComboBox != null) {

            roomComboBox.valueProperty()
                    .addListener(
                            (observable,
                             oldRoom,
                             newRoom) -> {

                                updateBookingProgress();
                            }
                    );
        }


        // ---------------------------------------------------------
        // DEFAULT BOOKING TYPE
        // ---------------------------------------------------------

        if (regularRadioButton != null
                && vipRadioButton != null) {

            if (!regularRadioButton.isSelected()
                    && !vipRadioButton.isSelected()) {

                regularRadioButton.setSelected(true);
            }
        }


        // ---------------------------------------------------------
        // DATE FORMATTER
        // ---------------------------------------------------------

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );


        // ---------------------------------------------------------
        // CHECK-IN DATE FORMATTER
        // ---------------------------------------------------------

        if (checkInDatePicker != null) {

            checkInDatePicker.setConverter(
                    new javafx.util.StringConverter<LocalDate>() {

                        @Override
                        public String toString(
                                LocalDate date) {

                            if (date == null) {
                                return "";
                            }

                            return formatter.format(date);
                        }


                        @Override
                        public LocalDate fromString(
                                String text) {

                            if (text == null
                                    || text.trim().isEmpty()) {

                                return null;
                            }

                            return LocalDate.parse(
                                    text,
                                    formatter
                            );
                        }
                    }
            );
        }


        // ---------------------------------------------------------
        // CHECK-OUT DATE FORMATTER
        // ---------------------------------------------------------

        if (checkOutDatePicker != null) {

            checkOutDatePicker.setConverter(
                    new javafx.util.StringConverter<LocalDate>() {

                        @Override
                        public String toString(
                                LocalDate date) {

                            if (date == null) {
                                return "";
                            }

                            return formatter.format(date);
                        }


                        @Override
                        public LocalDate fromString(
                                String text) {

                            if (text == null
                                    || text.trim().isEmpty()) {

                                return null;
                            }

                            return LocalDate.parse(
                                    text,
                                    formatter
                            );
                        }
                    }
            );
        }


        // ---------------------------------------------------------
        // PROGRESS BAR
        // ---------------------------------------------------------

        setupBookingProgress();


        // ---------------------------------------------------------
        // ROOM PRICE SLIDER
        // ---------------------------------------------------------

        setupRoomPriceSlider();


        // ---------------------------------------------------------
        // GUEST SPINNER
        // ---------------------------------------------------------

        setupGuestSpinner();
    }


    // =========================================================
    // LOAD BOOKINGS FROM DATABASE
    // =========================================================

    private void loadBookingsFromDatabase() {

        try {

            List<Booking> databaseBookings =
                    bookingDAO.getAllBookings();

            BookingData.bookingList.clear();

            BookingData.bookingList.addAll(
                    databaseBookings
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not load bookings from database."
            );
        }
    }


    // =========================================================
    // LOAD CUSTOMERS DIRECTLY FROM SQLITE
    // =========================================================

    private void loadCustomersIntoComboBox() {

        customerList.clear();

        try {

            List<Customer> customers =
                    customerDAO.getAllCustomers();

            customerList.addAll(customers);

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not load customers from database."
            );
        }


        if (customerComboBox != null) {

            customerComboBox.setItems(
                    customerList
            );


            // -----------------------------------------------------
            // SHOW CUSTOMER NAME IN COMBOBOX
            // -----------------------------------------------------

            customerComboBox.setConverter(
                    new javafx.util.StringConverter<Customer>() {

                        @Override
                        public String toString(
                                Customer customer) {

                            if (customer == null) {
                                return "";
                            }

                            return customer.getCustomerId()
                                    + " - "
                                    + customer.getName();
                        }


                        @Override
                        public Customer fromString(
                                String string) {

                            return null;
                        }
                    }
            );
        }
    }


    // =========================================================
    // LOAD ROOMS DIRECTLY FROM SQLITE
    // =========================================================

    private void loadRoomsIntoComboBox() {

        roomList.clear();

        try {

            List<Room> rooms =
                    roomDAO.getAllRooms();

            roomList.addAll(rooms);

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not load rooms from database."
            );
        }


        updateRoomComboBox();


        // ---------------------------------------------------------
        // ROOM CONVERTER
        // ---------------------------------------------------------

        if (roomComboBox != null) {

            roomComboBox.setConverter(
                    new javafx.util.StringConverter<Room>() {

                        @Override
                        public String toString(
                                Room room) {

                            if (room == null) {
                                return "";
                            }

                            return "Room "
                                    + room.getRoomNumber()
                                    + " - "
                                    + room.getRoomType()
                                    + " - "
                                    + room.getPrice();
                        }


                        @Override
                        public Room fromString(
                                String string) {

                            return null;
                        }
                    }
            );
        }
    }


    // =========================================================
    // UPDATE ROOM COMBOBOX
    // =========================================================

    private void updateRoomComboBox() {

        if (roomComboBox == null) {
            return;
        }

        double maximumPrice = 5000;

        if (roomPriceSlider != null) {

            maximumPrice =
                    roomPriceSlider.getValue();
        }

        ObservableList<Room> filteredRooms =
                FXCollections.observableArrayList();

        for (Room room : roomList) {

            if (room.getPrice() <= maximumPrice
                    && room.isAvailable()) {

                filteredRooms.add(room);
            }
        }

        roomComboBox.setItems(
                filteredRooms
        );
    }


    // =========================================================
    // UPDATE SELECTED CUSTOMER LABEL
    // =========================================================

    private void updateSelectedCustomerLabel() {

        if (selectedCustomerLabel == null) {
            return;
        }

        if (selectedCustomer == null) {

            selectedCustomerLabel.setText(
                    "No customer selected"
            );

            return;
        }

        selectedCustomerLabel.setText(
                "Selected Customer: ID = "
                        + selectedCustomer.getCustomerId()
                        + " | Name = "
                        + selectedCustomer.getName()
                        + " | Phone = "
                        + selectedCustomer.getPhone()
        );
    }


    // =========================================================
    // PASS CUSTOMER DATA
    // =========================================================

    public void setSelectedCustomer(
            Customer customer) {

        this.selectedCustomer = customer;

        if (customerComboBox != null) {

            // -----------------------------------------------------
            // Make sure customer exists in the ComboBox list
            // -----------------------------------------------------

            Customer databaseCustomer =
                    customerDAO.getCustomerById(
                            customer.getCustomerId()
                    );

            if (databaseCustomer != null) {

                selectedCustomer =
                        databaseCustomer;

                boolean exists = false;

                for (Customer c : customerList) {

                    if (c.getCustomerId()
                            == databaseCustomer.getCustomerId()) {

                        exists = true;
                        break;
                    }
                }

                if (!exists) {

                    customerList.add(
                            databaseCustomer
                    );
                }

                customerComboBox.setValue(
                        databaseCustomer
                );

            } else {

                if (!customerList.contains(customer)) {

                    customerList.add(customer);
                }

                customerComboBox.setValue(
                        customer
                );
            }
        }

        updateSelectedCustomerLabel();

        updateBookingProgress();
    }


    // =========================================================
    // PROGRESS BAR
    // =========================================================

    private void setupBookingProgress() {

        if (bookingProgressBar == null) {
            return;
        }

        bookingProgressBar.setProgress(0);


        if (customerComboBox != null) {

            customerComboBox.valueProperty()
                    .addListener(
                            (observable,
                             oldValue,
                             newValue) -> {

                                updateBookingProgress();
                            }
                    );
        }


        if (roomComboBox != null) {

            roomComboBox.valueProperty()
                    .addListener(
                            (observable,
                             oldValue,
                             newValue) -> {

                                updateBookingProgress();
                            }
                    );
        }


        if (checkInDatePicker != null) {

            checkInDatePicker.valueProperty()
                    .addListener(
                            (observable,
                             oldValue,
                             newValue) -> {

                                updateBookingProgress();
                            }
                    );
        }


        if (checkOutDatePicker != null) {

            checkOutDatePicker.valueProperty()
                    .addListener(
                            (observable,
                             oldValue,
                             newValue) -> {

                                updateBookingProgress();
                            }
                    );
        }


        updateBookingProgress();
    }


    // =========================================================
    // UPDATE PROGRESS BAR
    // =========================================================

    private void updateBookingProgress() {

        if (bookingProgressBar == null) {
            return;
        }

        double progress = 0.0;


        // ---------------------------------------------------------
        // STEP 1 - CUSTOMER
        // ---------------------------------------------------------

        Customer customer = null;

        if (selectedCustomer != null) {

            customer =
                    selectedCustomer;

        } else if (customerComboBox != null) {

            customer =
                    customerComboBox.getValue();
        }

        if (customer != null) {

            progress = 0.25;
        }


        // ---------------------------------------------------------
        // STEP 2 - ROOM
        // ---------------------------------------------------------

        if (roomComboBox != null
                && roomComboBox.getValue() != null) {

            progress = 0.50;
        }


        // ---------------------------------------------------------
        // STEP 3 - CHECK-IN
        // ---------------------------------------------------------

        if (checkInDatePicker != null
                && checkInDatePicker.getValue() != null) {

            progress = 0.75;
        }


        // ---------------------------------------------------------
        // STEP 4 - CHECK-OUT
        // ---------------------------------------------------------

        if (checkOutDatePicker != null
                && checkOutDatePicker.getValue() != null) {

            progress = 1.0;
        }


        bookingProgressBar.setProgress(
                progress
        );
    }


    // =========================================================
    // ROOM PRICE SLIDER
    // =========================================================

    private void setupRoomPriceSlider() {

        if (roomPriceSlider == null) {
            return;
        }

        roomPriceSlider.setMin(1000);

        roomPriceSlider.setMax(5000);

        roomPriceSlider.setValue(5000);


        if (roomPriceLabel != null) {

            roomPriceLabel.setText(
                    "Room Price Range: 5000"
            );
        }


        roomPriceSlider.valueProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            double price =
                                    newValue.doubleValue();


                            if (roomPriceLabel != null) {

                                roomPriceLabel.setText(
                                        "Room Price Range: "
                                                + (int) price
                                );
                            }


                            filterRoomsByPrice(
                                    price
                            );
                        }
                );
    }


    // =========================================================
    // FILTER ROOMS BY PRICE
    // =========================================================

    private void filterRoomsByPrice(
            double maximumPrice) {

        if (roomComboBox == null) {
            return;
        }

        ObservableList<Room> filteredRooms =
                FXCollections.observableArrayList();

        for (Room room : roomList) {

            if (room.getPrice()
                    <= maximumPrice
                    && room.isAvailable()) {

                filteredRooms.add(room);
            }
        }

        roomComboBox.setItems(
                filteredRooms
        );
    }


    // =========================================================
    // GUEST SPINNER
    // =========================================================

    private void setupGuestSpinner() {

        if (guestSpinner == null) {
            return;
        }

        guestSpinner.setValueFactory(
                new SpinnerValueFactory
                        .IntegerSpinnerValueFactory(
                        1,
                        10,
                        1
                )
        );
    }


    // =========================================================
    // ADD BOOKING
    // =========================================================

    @FXML
    private void addBooking() {

        // ---------------------------------------------------------
        // BOOKING ID
        // ---------------------------------------------------------

        TextInputDialog bookingIdDialog =
                new TextInputDialog();

        bookingIdDialog.setTitle(
                "Add Booking"
        );

        bookingIdDialog.setHeaderText(
                "Enter Booking ID"
        );

        bookingIdDialog.setContentText(
                "Booking ID:"
        );


        Optional<String> bookingIdResult =
                bookingIdDialog.showAndWait();

        if (bookingIdResult.isEmpty()) {
            return;
        }


        try {

            // ---------------------------------------------------------
            // BOOKING ID
            // ---------------------------------------------------------

            int bookingId =
                    Integer.parseInt(
                            bookingIdResult
                                    .get()
                                    .trim()
                    );


            // ---------------------------------------------------------
            // CHECK DUPLICATE BOOKING ID
            // ---------------------------------------------------------

            if (bookingDAO.getBookingById(
                    bookingId
            ) != null) {

                showWarning(
                        "Warning",
                        "Booking ID already exists."
                );

                return;
            }


            // ---------------------------------------------------------
            // CUSTOMER
            // ---------------------------------------------------------

            Customer customer;

            if (selectedCustomer != null) {

                customer =
                        selectedCustomer;

            } else {

                customer =
                        customerComboBox.getValue();

                if (customer == null) {

                    showWarning(
                            "Warning",
                            "Please select a customer."
                    );

                    return;
                }
            }


            // ---------------------------------------------------------
            // GET LATEST CUSTOMER FROM DATABASE
            // ---------------------------------------------------------

            Customer databaseCustomer =
                    customerDAO.getCustomerById(
                            customer.getCustomerId()
                    );

            if (databaseCustomer == null) {

                showError(
                        "Database Error",
                        "Selected customer does not exist in database."
                );

                return;
            }

            customer =
                    databaseCustomer;


            // ---------------------------------------------------------
            // ROOM
            // ---------------------------------------------------------

            Room selectedRoom =
                    roomComboBox.getValue();

            if (selectedRoom == null) {

                showWarning(
                        "Warning",
                        "Please select a room."
                );

                return;
            }


            // ---------------------------------------------------------
            // GET LATEST ROOM FROM DATABASE
            // ---------------------------------------------------------

            Room room =
                    roomDAO.getRoomByNumber(
                            selectedRoom.getRoomNumber()
                    );

            if (room == null) {

                showError(
                        "Database Error",
                        "Selected room does not exist in database."
                );

                return;
            }


            // ---------------------------------------------------------
            // CHECK ROOM AVAILABILITY
            // ---------------------------------------------------------

            if (!room.isAvailable()) {

                showWarning(
                        "Room Unavailable",
                        "The selected room is already booked."
                );

                loadRoomsIntoComboBox();

                return;
            }


            // ---------------------------------------------------------
            // CHECK-IN DATE
            // ---------------------------------------------------------

            LocalDate checkIn =
                    checkInDatePicker.getValue();

            if (checkIn == null) {

                showWarning(
                        "Warning",
                        "Please select check-in date."
                );

                return;
            }


            // ---------------------------------------------------------
            // CHECK-OUT DATE
            // ---------------------------------------------------------

            LocalDate checkOut =
                    checkOutDatePicker.getValue();

            if (checkOut == null) {

                showWarning(
                        "Warning",
                        "Please select check-out date."
                );

                return;
            }


            // ---------------------------------------------------------
            // DATE VALIDATION
            // ---------------------------------------------------------

            if (!checkOut.isAfter(checkIn)) {

                showError(
                        "Error",
                        "Check-out date must be after check-in date."
                );

                return;
            }


            // ---------------------------------------------------------
            // BOOKING TYPE
            // ---------------------------------------------------------

            String bookingType =
                    "Regular";

            if (vipRadioButton != null
                    && vipRadioButton.isSelected()) {

                bookingType = "VIP";
            }


            // ---------------------------------------------------------
            // EXTRA SERVICES
            // ---------------------------------------------------------

            String extraServices =
                    "None";

            boolean breakfast =
                    breakfastCheckBox != null
                            && breakfastCheckBox.isSelected();

            boolean airportPickup =
                    airportPickupCheckBox != null
                            && airportPickupCheckBox.isSelected();


            if (breakfast
                    && airportPickup) {

                extraServices =
                        "Breakfast, Airport Pickup";

            } else if (breakfast) {

                extraServices =
                        "Breakfast";

            } else if (airportPickup) {

                extraServices =
                        "Airport Pickup";
            }


            // ---------------------------------------------------------
            // NUMBER OF GUESTS
            // ---------------------------------------------------------

            int numberOfGuests = 1;

            if (guestSpinner != null
                    && guestSpinner.getValue() != null) {

                numberOfGuests =
                        guestSpinner.getValue();
            }


            // ---------------------------------------------------------
            // FINAL VALUES FOR LAMBDA
            // ---------------------------------------------------------

            final String finalBookingType =
                    bookingType;

            final String finalExtraServices =
                    extraServices;

            final int finalNumberOfGuests =
                    numberOfGuests;


            // =========================================================
            // MULTITHREADING
            // =========================================================

            Future<Booking> future =
                    bookingService.createBookingAsync(
                            bookingId,
                            customer,
                            room,
                            checkIn,
                            checkOut
                    );


            // =========================================================
            // WAIT FOR BOOKING RESULT
            // =========================================================

            Thread resultThread =
                    new Thread(() -> {

                        try {

                            Booking booking =
                                    future.get();


                            // -------------------------------------------------
                            // SAVE BOOKING TO SQLITE
                            // -------------------------------------------------

                            bookingDAO.insertBooking(
                                    booking
                            );


                            // -------------------------------------------------
                            // ADD INCOME TO FINANCIAL RECORD
                            // -------------------------------------------------

                            if (!financialDAO.incomeExistsForBooking(
                                    booking.getBookingId()
                            )) {

                                double incomeAmount =
                                        booking.getRoom().getPrice();

                                String incomeReason =
                                        "Room Booking - Room "
                                                + booking.getRoom()
                                                .getRoomNumber()
                                                + " - Customer "
                                                + booking.getCustomer()
                                                .getName();

                                boolean incomeSaved =
                                        financialDAO.addIncome(
                                                LocalDate.now(),
                                                incomeAmount,
                                                incomeReason,
                                                booking.getBookingId()
                                        );

                                if (!incomeSaved) {

                                    throw new RuntimeException(
                                            "Booking was saved, but financial income could not be recorded."
                                    );
                                }
                            }


                            // -------------------------------------------------
                            // UPDATE ROOM AVAILABILITY IN SQLITE
                            // -------------------------------------------------

                            roomDAO.updateAvailability(
                                    room.getRoomNumber(),
                                    false
                            );


                            // -------------------------------------------------
                            // UPDATE JAVAFX UI
                            // -------------------------------------------------

                            Platform.runLater(() -> {

                                BookingData.bookingList.add(
                                        booking
                                );


                                // -------------------------------------------------
                                // UPDATE LOCAL ROOM
                                // -------------------------------------------------

                                room.setAvailable(false);


                                // -------------------------------------------------
                                // REFRESH ROOM LIST
                                // -------------------------------------------------

                                loadRoomsIntoComboBox();


                                // -------------------------------------------------
                                // SUCCESS MESSAGE
                                // -------------------------------------------------

                                showMessage(
                                        "Success",
                                        "Booking added successfully!\n\n"
                                                + "Booking Type: "
                                                + finalBookingType
                                                + "\n"
                                                + "Extra Services: "
                                                + finalExtraServices
                                                + "\n"
                                                + "Number of Guests: "
                                                + finalNumberOfGuests
                                );


                                // -------------------------------------------------
                                // CLEAR DATE FIELDS
                                // -------------------------------------------------

                                checkInDatePicker.setValue(
                                        null
                                );

                                checkOutDatePicker.setValue(
                                        null
                                );


                                // -------------------------------------------------
                                // RESET BOOKING TYPE
                                // -------------------------------------------------

                                if (regularRadioButton != null) {

                                    regularRadioButton.setSelected(
                                            true
                                    );
                                }


                                // -------------------------------------------------
                                // RESET BREAKFAST
                                // -------------------------------------------------

                                if (breakfastCheckBox != null) {

                                    breakfastCheckBox.setSelected(
                                            false
                                    );
                                }


                                // -------------------------------------------------
                                // RESET AIRPORT PICKUP
                                // -------------------------------------------------

                                if (airportPickupCheckBox != null) {

                                    airportPickupCheckBox.setSelected(
                                            false
                                    );
                                }


                                // -------------------------------------------------
                                // RESET GUEST SPINNER
                                // -------------------------------------------------

                                if (guestSpinner != null
                                        && guestSpinner
                                        .getValueFactory()
                                        != null) {

                                    guestSpinner
                                            .getValueFactory()
                                            .setValue(1);
                                }


                                // -------------------------------------------------
                                // UPDATE PROGRESS
                                // -------------------------------------------------

                                updateBookingProgress();

                            });

                        } catch (Exception e) {

                            e.printStackTrace();

                            Platform.runLater(() -> {

                                showError(
                                        "Booking Error",
                                        "Could not save booking to database."
                                );

                            });
                        }

                    });


            // ---------------------------------------------------------
            // MAKE THREAD DAEMON
            // ---------------------------------------------------------

            resultThread.setDaemon(true);


            // ---------------------------------------------------------
            // START THREAD
            // ---------------------------------------------------------

            resultThread.start();


        } catch (NumberFormatException e) {

            showError(
                    "Error",
                    "Please enter a valid Booking ID."
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Error",
                    "Something went wrong while adding the booking."
            );
        }
    }


    // =========================================================
    // UPDATE BOOKING
    // =========================================================

    @FXML
    private void updateBooking() {

        Booking selectedBooking =
                bookingTable
                        .getSelectionModel()
                        .getSelectedItem();


        // ---------------------------------------------------------
        // CHECK SELECTION
        // ---------------------------------------------------------

        if (selectedBooking == null) {

            showWarning(
                    "Warning",
                    "Please select a booking first."
            );

            return;
        }


        // ---------------------------------------------------------
        // NEW CHECK-IN DATE
        // ---------------------------------------------------------

        TextInputDialog checkInDialog =
                new TextInputDialog(
                        selectedBooking
                                .getCheckIn()
                                .toString()
                );

        checkInDialog.setTitle(
                "Update Booking"
        );

        checkInDialog.setHeaderText(
                "Update Check-In Date"
        );

        checkInDialog.setContentText(
                "YYYY-MM-DD:"
        );


        Optional<String> checkInResult =
                checkInDialog.showAndWait();

        if (checkInResult.isEmpty()) {
            return;
        }


        // ---------------------------------------------------------
        // NEW CHECK-OUT DATE
        // ---------------------------------------------------------

        TextInputDialog checkOutDialog =
                new TextInputDialog(
                        selectedBooking
                                .getCheckOut()
                                .toString()
                );

        checkOutDialog.setTitle(
                "Update Booking"
        );

        checkOutDialog.setHeaderText(
                "Update Check-Out Date"
        );

        checkOutDialog.setContentText(
                "YYYY-MM-DD:"
        );


        Optional<String> checkOutResult =
                checkOutDialog.showAndWait();

        if (checkOutResult.isEmpty()) {
            return;
        }


        try {

            // ---------------------------------------------------------
            // CONVERT DATES
            // ---------------------------------------------------------

            LocalDate newCheckIn =
                    LocalDate.parse(
                            checkInResult
                                    .get()
                                    .trim()
                    );

            LocalDate newCheckOut =
                    LocalDate.parse(
                            checkOutResult
                                    .get()
                                    .trim()
                    );


            // ---------------------------------------------------------
            // VALIDATE DATES
            // ---------------------------------------------------------

            if (!newCheckOut.isAfter(newCheckIn)) {

                showError(
                        "Error",
                        "Check-out date must be after check-in date."
                );

                return;
            }


            // ---------------------------------------------------------
            // UPDATE BOOKING OBJECT
            // ---------------------------------------------------------

            selectedBooking.setCheckIn(
                    newCheckIn
            );

            selectedBooking.setCheckOut(
                    newCheckOut
            );


            // ---------------------------------------------------------
            // UPDATE DATABASE
            // ---------------------------------------------------------

            bookingDAO.updateBooking(
                    selectedBooking
            );


            // ---------------------------------------------------------
            // REFRESH TABLE
            // ---------------------------------------------------------

            bookingTable.refresh();


            showMessage(
                    "Success",
                    "Booking updated successfully!"
            );


        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Error",
                    "Please enter dates in YYYY-MM-DD format."
            );
        }
    }


    // =========================================================
    // DELETE BOOKING
    // =========================================================

    @FXML
    private void deleteBooking() {

        Booking selectedBooking =
                bookingTable
                        .getSelectionModel()
                        .getSelectedItem();


        // ---------------------------------------------------------
        // CHECK SELECTION
        // ---------------------------------------------------------

        if (selectedBooking == null) {

            showWarning(
                    "Warning",
                    "Please select a booking first."
            );

            return;
        }


        // ---------------------------------------------------------
        // CONFIRMATION ALERT
        // ---------------------------------------------------------

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Delete Booking"
        );

        confirmation.setHeaderText(
                "Delete Booking "
                        + selectedBooking.getBookingId()
                        + "?"
        );

        confirmation.setContentText(
                "Are you sure you want to delete this booking?"
        );


        Optional<ButtonType> result =
                confirmation.showAndWait();


        // ---------------------------------------------------------
        // DELETE
        // ---------------------------------------------------------

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            try {

                // -----------------------------------------------------
                // DELETE FINANCIAL RECORD FIRST
                // -----------------------------------------------------

                boolean financialDeleted =
                        financialDAO.deleteIncomeForBooking(
                                selectedBooking.getBookingId()
                        );

                if (!financialDeleted) {

                    throw new RuntimeException(
                            "Could not delete financial record for booking."
                    );
                }


                // -----------------------------------------------------
                // DELETE BOOKING FROM DATABASE
                // -----------------------------------------------------

                bookingDAO.deleteBooking(
                        selectedBooking.getBookingId()
                );


                // -----------------------------------------------------
                // MAKE ROOM AVAILABLE IN SQLITE
                // -----------------------------------------------------

                if (selectedBooking.getRoom() != null) {

                    int roomNumber =
                            selectedBooking
                                    .getRoom()
                                    .getRoomNumber();

                    roomDAO.updateAvailability(
                            roomNumber,
                            true
                    );

                    selectedBooking
                            .getRoom()
                            .setAvailable(true);
                }


                // -----------------------------------------------------
                // REMOVE FROM JAVAFX LIST
                // -----------------------------------------------------

                BookingData.bookingList.remove(
                        selectedBooking
                );


                // -----------------------------------------------------
                // RELOAD ROOMS
                // -----------------------------------------------------

                loadRoomsIntoComboBox();


                // -----------------------------------------------------
                // SUCCESS MESSAGE
                // -----------------------------------------------------

                showMessage(
                        "Success",
                        "Booking deleted successfully!"
                );


            } catch (Exception e) {

                e.printStackTrace();

                showError(
                        "Database Error",
                        "Could not delete booking."
                );
            }
        }
    }


    // =========================================================
    // BACK TO MAIN
    // =========================================================

    @FXML
    private void backToMain() {

        Stage stage =
                (Stage) bookingTable
                        .getScene()
                        .getWindow();

        stage.close();
    }


    // =========================================================
    // WARNING MESSAGE
    // =========================================================

    private void showWarning(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
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


    // =========================================================
    // INFORMATION MESSAGE
    // =========================================================

    private void showMessage(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }


    // =========================================================
    // SHUTDOWN BOOKING SERVICE
    // =========================================================

    public void shutdownBookingService() {

        bookingService.shutdown();
    }
}