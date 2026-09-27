package hotel.controller;

import hotel.database.CustomerDAO;
import hotel.model.Customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;


/**
 * Controller for Customer Management page.
 *
 * The main Customer Management page is responsible for:
 *
 * 1. Showing all customers
 * 2. Live searching customers
 * 3. Opening Add Customer page
 * 4. Opening Update Customer page
 * 5. Deleting customers
 * 6. Opening Booking page for selected customer
 * 7. Returning to Main Dashboard
 */
public class CustomerController {

    // =========================================================
    // FXML CONTROLS
    // =========================================================

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> phoneColumn;

    @FXML
    private TableColumn<Customer, String> emailColumn;

    @FXML
    private TableColumn<Customer, String> addressColumn;


    // =========================================================
    // CUSTOMER DATA
    // =========================================================

    private final ObservableList<Customer> customerList =
            FXCollections.observableArrayList();

    private final ObservableList<Customer> filteredCustomerList =
            FXCollections.observableArrayList();


    // =========================================================
    // CUSTOMER DAO
    // =========================================================

    private final CustomerDAO customerDAO =
            new CustomerDAO();


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        // =====================================================
        // CONNECT TABLE COLUMNS
        // =====================================================

        customerIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("customerId")
        );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        addressColumn.setCellValueFactory(
                new PropertyValueFactory<>("address")
        );


        // =====================================================
        // CONNECT FILTERED LIST WITH TABLE
        // =====================================================

        customerTable.setItems(filteredCustomerList);


        // =====================================================
        // LOAD CUSTOMERS FROM DATABASE
        // =====================================================

        loadCustomersFromDatabase();


        // =====================================================
        // LIVE SEARCH
        // =====================================================

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    filterCustomers(newValue);
                }
        );
    }


    // =========================================================
    // LOAD CUSTOMERS FROM SQLITE DATABASE
    // =========================================================

    private void loadCustomersFromDatabase() {

        try {

            List<Customer> customers =
                    customerDAO.getAllCustomers();

            customerList.clear();

            customerList.addAll(customers);

            // Initially show all customers
            filteredCustomerList.setAll(
                    customerList
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not load customers from database."
            );
        }
    }


    // =========================================================
    // LIVE CUSTOMER SEARCH
    // =========================================================

    private void filterCustomers(String searchText) {

        // Clear current displayed results
        filteredCustomerList.clear();


        // If search box is empty, show all customers
        if (searchText == null ||
                searchText.trim().isEmpty()) {

            filteredCustomerList.addAll(
                    customerList
            );

            return;
        }


        // Convert search text to lowercase
        String search =
                searchText
                        .trim()
                        .toLowerCase();


        // =====================================================
        // CHECK EVERY CUSTOMER
        // =====================================================

        for (Customer customer : customerList) {

            String id =
                    String.valueOf(
                            customer.getCustomerId()
                    ).toLowerCase();

            String name =
                    safeString(
                            customer.getName()
                    ).toLowerCase();

            String phone =
                    safeString(
                            customer.getPhone()
                    ).toLowerCase();

            String email =
                    safeString(
                            customer.getEmail()
                    ).toLowerCase();

            String address =
                    safeString(
                            customer.getAddress()
                    ).toLowerCase();


            // =================================================
            // MATCH ANY FIELD
            // =================================================

            if (id.contains(search)
                    || name.contains(search)
                    || phone.contains(search)
                    || email.contains(search)
                    || address.contains(search)) {

                filteredCustomerList.add(
                        customer
                );
            }
        }
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeString(String value) {

        if (value == null) {
            return "";
        }

        return value;
    }


    // =========================================================
    // ADD CUSTOMER
    // =========================================================

    @FXML
    private void addCustomer() {

        try {

            // =================================================
            // LOAD ADD CUSTOMER PAGE
            // =================================================

            FXMLLoader loader =
                    new FXMLLoader(
                            CustomerController.class.getResource(
                                    "/view/AddCustomerView.fxml"
                            )
                    );


            Scene scene =
                    new Scene(
                            loader.load()
                    );


            // =================================================
            // CREATE NEW WINDOW
            // =================================================

            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Add Customer"
            );

            stage.setScene(scene);

            stage.setWidth(850);

            stage.setHeight(650);

            stage.setMinWidth(750);

            stage.setMinHeight(550);

            stage.show();


            // =================================================
            // REFRESH WHEN WINDOW CLOSES
            // =================================================

            stage.setOnHidden(
                    event -> loadCustomersFromDatabase()
            );


        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Error",
                    "Could not open Add Customer page."
            );
        }
    }


    // =========================================================
    // UPDATE CUSTOMER
    // =========================================================

    @FXML
    private void updateCustomer() {

        // =====================================================
        // GET SELECTED CUSTOMER
        // =====================================================

        Customer selectedCustomer =
                customerTable
                        .getSelectionModel()
                        .getSelectedItem();


        // =====================================================
        // CHECK SELECTION
        // =====================================================

        if (selectedCustomer == null) {

            showWarning(
                    "No Customer Selected",
                    "Please select a customer first."
            );

            return;
        }


        try {

            // =================================================
            // LOAD UPDATE CUSTOMER PAGE
            // =================================================

            FXMLLoader loader =
                    new FXMLLoader(
                            CustomerController.class.getResource(
                                    "/view/UpdateCustomerView.fxml"
                            )
                    );


            Scene scene =
                    new Scene(
                            loader.load()
                    );


            // =================================================
            // GET UPDATE CONTROLLER
            // =================================================

            UpdateCustomerController controller =
                    loader.getController();


            // =================================================
            // PASS SELECTED CUSTOMER
            // =================================================

            controller.setCustomer(
                    selectedCustomer
            );


            // =================================================
            // CREATE UPDATE WINDOW
            // =================================================

            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Update Customer"
            );

            stage.setScene(scene);

            stage.setWidth(850);

            stage.setHeight(650);

            stage.setMinWidth(750);

            stage.setMinHeight(550);

            stage.show();


            // =================================================
            // REFRESH TABLE AFTER UPDATE
            // =================================================

            stage.setOnHidden(
                    event -> loadCustomersFromDatabase()
            );


        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Error",
                    "Could not open Update Customer page."
            );
        }
    }


    // =========================================================
    // DELETE CUSTOMER
    // =========================================================

    @FXML
    private void deleteCustomer() {

        // =====================================================
        // GET SELECTED CUSTOMER
        // =====================================================

        Customer selectedCustomer =
                customerTable
                        .getSelectionModel()
                        .getSelectedItem();


        // =====================================================
        // CHECK SELECTION
        // =====================================================

        if (selectedCustomer == null) {

            showWarning(
                    "No Customer Selected",
                    "Please select a customer first."
            );

            return;
        }


        // =====================================================
        // CONFIRM DELETE
        // =====================================================

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Delete Customer"
        );


        confirmation.setHeaderText(
                "Delete Customer "
                        + selectedCustomer.getName()
                        + "?"
        );


        confirmation.setContentText(
                "Are you sure you want to delete this customer?"
        );


        Optional<ButtonType> result =
                confirmation.showAndWait();


        // =====================================================
        // DELETE IF USER PRESSES OK
        // =====================================================

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            try {

                customerDAO.deleteCustomer(
                        selectedCustomer.getCustomerId()
                );


                // =================================================
                // RELOAD CUSTOMER LIST
                // =================================================

                loadCustomersFromDatabase();


                // =================================================
                // SUCCESS MESSAGE
                // =================================================

                showInformation(
                        "Success",
                        "Customer deleted successfully!"
                );


            } catch (RuntimeException e) {

                e.printStackTrace();

                showError(
                        "Database Error",
                        "Could not delete customer."
                );
            }
        }
    }


    // =========================================================
    // BOOK SELECTED CUSTOMER
    // =========================================================

    @FXML
    private void bookSelectedCustomer() {

        // =====================================================
        // GET SELECTED CUSTOMER
        // =====================================================

        Customer selectedCustomer =
                customerTable
                        .getSelectionModel()
                        .getSelectedItem();


        // =====================================================
        // CHECK SELECTION
        // =====================================================

        if (selectedCustomer == null) {

            showWarning(
                    "No Customer Selected",
                    "Please select a customer first."
            );

            return;
        }


        try {

            // =================================================
            // LOAD BOOKING PAGE
            // =================================================

            FXMLLoader loader =
                    new FXMLLoader(
                            CustomerController.class.getResource(
                                    "/view/BookingView.fxml"
                            )
                    );


            Scene scene =
                    new Scene(
                            loader.load()
                    );


            // =================================================
            // GET BOOKING CONTROLLER
            // =================================================

            BookingController controller =
                    loader.getController();


            // =================================================
            // PASS CUSTOMER TO BOOKING CONTROLLER
            // =================================================

            controller.setSelectedCustomer(
                    selectedCustomer
            );


            // =================================================
            // OPEN BOOKING WINDOW
            // =================================================

            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Booking Management"
            );

            stage.setScene(scene);

            stage.setWidth(900);

            stage.setHeight(700);

            stage.show();


        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Error",
                    "Could not open Booking Management."
            );
        }
    }


    // =========================================================
    // BACK TO MAIN DASHBOARD
    // =========================================================

    @FXML
    private void backToMain() {

        Stage stage =
                (Stage) customerTable
                        .getScene()
                        .getWindow();


        stage.close();
    }


    // =========================================================
    // INFORMATION MESSAGE
    // =========================================================

    private void showInformation(
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
}