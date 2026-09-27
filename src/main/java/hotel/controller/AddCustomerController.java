package hotel.controller;

import hotel.database.CustomerDAO;
import hotel.model.Customer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class AddCustomerController {

    // =========================================================
    // FXML FIELDS
    // =========================================================

    @FXML
    private TextField customerIdField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField addressField;

    @FXML
    private ImageView customerImageView;

    @FXML
    private TextArea specialRequestTextArea;


    // =========================================================
    // DATABASE
    // =========================================================

    private final CustomerDAO customerDAO = new CustomerDAO();

    // Stores the selected image file path
    private String selectedPhotoPath;


    // =========================================================
    // BROWSE IMAGE
    // =========================================================

    @FXML
    private void browseImage() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select Customer Photo");

        // Allow image files only
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                );

        fileChooser.getExtensionFilters().add(imageFilter);

        Stage stage = (Stage) customerImageView
                .getScene()
                .getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {

            selectedPhotoPath = selectedFile.getAbsolutePath();

            Image image = new Image(
                    selectedFile.toURI().toString()
            );

            customerImageView.setImage(image);
        }
    }


    // =========================================================
    // SAVE CUSTOMER
    // =========================================================

    @FXML
    private void saveCustomer() {

        // -----------------------------------------------------
        // Get values from text fields
        // -----------------------------------------------------

        String idText = customerIdField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        // Special request is currently UI-only.
        // Customer model/database does not have this field.
        String specialRequest =
                specialRequestTextArea.getText().trim();


        // -----------------------------------------------------
        // Validate Customer ID
        // -----------------------------------------------------

        if (idText.isEmpty()) {

            showWarning(
                    "Missing Customer ID",
                    "Please enter a customer ID."
            );

            customerIdField.requestFocus();
            return;
        }

        int customerId;

        try {

            customerId = Integer.parseInt(idText);

        } catch (NumberFormatException e) {

            showWarning(
                    "Invalid Customer ID",
                    "Customer ID must be a number."
            );

            customerIdField.requestFocus();
            return;
        }


        // -----------------------------------------------------
        // Validate Name
        // -----------------------------------------------------

        if (name.isEmpty()) {

            showWarning(
                    "Missing Name",
                    "Please enter the customer's name."
            );

            nameField.requestFocus();
            return;
        }


        // -----------------------------------------------------
        // Check duplicate Customer ID
        // -----------------------------------------------------

        try {

            Customer existingCustomer =
                    customerDAO.getCustomerById(customerId);

            if (existingCustomer != null) {

                showWarning(
                        "Duplicate Customer ID",
                        "Customer ID " + customerId +
                                " already exists."
                );

                customerIdField.requestFocus();
                return;
            }

        } catch (RuntimeException e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not check the customer ID."
            );

            return;
        }


        // -----------------------------------------------------
        // Create Customer object
        // -----------------------------------------------------

        Customer customer = new Customer(
                customerId,
                name,
                phone,
                email,
                address
        );


        // -----------------------------------------------------
        // Save photo path
        // -----------------------------------------------------

        if (selectedPhotoPath != null) {

            customer.setPhotoPath(selectedPhotoPath);
        }


        // -----------------------------------------------------
        // Save customer to database
        // -----------------------------------------------------

        try {

            customerDAO.addCustomer(customer);

            showInformation(
                    "Success",
                    "Customer added successfully!"
            );

            closeWindow();

        } catch (RuntimeException e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not save the customer."
            );
        }
    }


    // =========================================================
    // CANCEL
    // =========================================================

    @FXML
    private void cancel() {

        closeWindow();
    }


    // =========================================================
    // CLOSE WINDOW
    // =========================================================

    private void closeWindow() {

        Stage stage =
                (Stage) customerIdField
                        .getScene()
                        .getWindow();

        stage.close();
    }


    // =========================================================
    // INFORMATION ALERT
    // =========================================================

    private void showInformation(
            String title,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    // =========================================================
    // WARNING ALERT
    // =========================================================

    private void showWarning(
            String title,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.WARNING);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    // =========================================================
    // ERROR ALERT
    // =========================================================

    private void showError(
            String title,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}