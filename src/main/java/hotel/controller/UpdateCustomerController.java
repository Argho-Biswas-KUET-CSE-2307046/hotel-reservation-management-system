package hotel.controller;

import hotel.database.CustomerDAO;
import hotel.model.Customer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class UpdateCustomerController {

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


    // =========================================================
    // SELECTED CUSTOMER
    // =========================================================

    private Customer selectedCustomer;


    // =========================================================
    // SELECTED PHOTO
    // =========================================================

    private String selectedPhotoPath;


    // =========================================================
    // SET CUSTOMER
    // =========================================================

    public void setCustomer(Customer customer) {

        this.selectedCustomer = customer;

        loadCustomerData();
    }


    // =========================================================
    // LOAD CUSTOMER DATA INTO FORM
    // =========================================================

    private void loadCustomerData() {

        if (selectedCustomer == null) {
            return;
        }


        // -----------------------------------------------------
        // CUSTOMER ID
        // -----------------------------------------------------

        customerIdField.setText(
                String.valueOf(
                        selectedCustomer.getCustomerId()
                )
        );


        // -----------------------------------------------------
        // NAME
        // -----------------------------------------------------

        nameField.setText(
                safeString(
                        selectedCustomer.getName()
                )
        );


        // -----------------------------------------------------
        // PHONE
        // -----------------------------------------------------

        phoneField.setText(
                safeString(
                        selectedCustomer.getPhone()
                )
        );


        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        emailField.setText(
                safeString(
                        selectedCustomer.getEmail()
                )
        );


        // -----------------------------------------------------
        // ADDRESS
        // -----------------------------------------------------

        addressField.setText(
                safeString(
                        selectedCustomer.getAddress()
                )
        );


        // -----------------------------------------------------
        // PHOTO
        // -----------------------------------------------------

        String photoPath =
                selectedCustomer.getPhotoPath();

        if (photoPath != null
                && !photoPath.isEmpty()) {

            selectedPhotoPath = photoPath;

            try {

                Image image =
                        new Image(photoPath);

                customerImageView.setImage(image);

            } catch (Exception e) {

                customerImageView.setImage(null);
            }
        }
    }


    // =========================================================
    // BROWSE IMAGE
    // =========================================================

    @FXML
    private void browseImage() {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Select Customer Photo"
        );


        // -----------------------------------------------------
        // IMAGE FILTER
        // -----------------------------------------------------

        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                );

        fileChooser
                .getExtensionFilters()
                .add(imageFilter);


        // -----------------------------------------------------
        // GET CURRENT WINDOW
        // -----------------------------------------------------

        Stage stage =
                (Stage) customerImageView
                        .getScene()
                        .getWindow();


        File selectedFile =
                fileChooser.showOpenDialog(stage);


        // -----------------------------------------------------
        // CHECK FILE
        // -----------------------------------------------------

        if (selectedFile == null) {
            return;
        }


        // -----------------------------------------------------
        // SAVE PHOTO PATH
        // -----------------------------------------------------

        selectedPhotoPath =
                selectedFile.toURI().toString();


        // -----------------------------------------------------
        // DISPLAY IMAGE
        // -----------------------------------------------------

        try {

            Image image =
                    new Image(selectedPhotoPath);

            customerImageView.setImage(image);

        } catch (Exception e) {

            customerImageView.setImage(null);

            showError(
                    "Image Error",
                    "Could not load the selected image."
            );
        }
    }


    // =========================================================
    // UPDATE CUSTOMER
    // =========================================================

    @FXML
    private void updateCustomer() {

        if (selectedCustomer == null) {

            showError(
                    "Error",
                    "No customer was selected."
            );

            return;
        }


        // -----------------------------------------------------
        // GET FORM VALUES
        // -----------------------------------------------------

        String idText =
                customerIdField
                        .getText()
                        .trim();

        String name =
                nameField
                        .getText()
                        .trim();

        String phone =
                phoneField
                        .getText()
                        .trim();

        String email =
                emailField
                        .getText()
                        .trim();

        String address =
                addressField
                        .getText()
                        .trim();


        // -----------------------------------------------------
        // VALIDATE CUSTOMER ID
        // -----------------------------------------------------

        if (idText.isEmpty()) {

            showWarning(
                    "Input Error",
                    "Customer ID cannot be empty."
            );

            customerIdField.requestFocus();

            return;
        }


        int customerId;

        try {

            customerId =
                    Integer.parseInt(idText);

        } catch (NumberFormatException e) {

            showWarning(
                    "Input Error",
                    "Customer ID must be a valid number."
            );

            customerIdField.requestFocus();

            return;
        }


        // -----------------------------------------------------
        // CUSTOMER ID SHOULD NOT CHANGE
        // -----------------------------------------------------

        if (customerId
                != selectedCustomer.getCustomerId()) {

            showWarning(
                    "Customer ID",
                    "Customer ID cannot be changed during update."
            );

            customerIdField.setText(
                    String.valueOf(
                            selectedCustomer.getCustomerId()
                    )
            );

            customerIdField.requestFocus();

            return;
        }


        // -----------------------------------------------------
        // VALIDATE NAME
        // -----------------------------------------------------

        if (name.isEmpty()) {

            showWarning(
                    "Input Error",
                    "Customer name cannot be empty."
            );

            nameField.requestFocus();

            return;
        }


        // -----------------------------------------------------
        // UPDATE CUSTOMER OBJECT
        // -----------------------------------------------------

        selectedCustomer.setName(name);

        selectedCustomer.setPhone(phone);

        selectedCustomer.setEmail(email);

        selectedCustomer.setAddress(address);


        // -----------------------------------------------------
        // UPDATE PHOTO
        // -----------------------------------------------------

        if (selectedPhotoPath != null
                && !selectedPhotoPath.isEmpty()) {

            selectedCustomer.setPhotoPath(
                    selectedPhotoPath
            );
        }


        // -----------------------------------------------------
        // UPDATE DATABASE
        // -----------------------------------------------------

        try {

            customerDAO.updateCustomer(
                    selectedCustomer
            );


            // -------------------------------------------------
            // SUCCESS MESSAGE
            // -------------------------------------------------

            showInformation(
                    "Success",
                    "Customer updated successfully!"
            );


            // -------------------------------------------------
            // CLOSE WINDOW
            // -------------------------------------------------

            closeWindow();

        } catch (RuntimeException e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not update customer."
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
    // SAFE STRING
    // =========================================================

    private String safeString(String value) {

        if (value == null) {
            return "";
        }

        return value;
    }


    // =========================================================
    // INFORMATION ALERT
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
    // WARNING ALERT
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
    // ERROR ALERT
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