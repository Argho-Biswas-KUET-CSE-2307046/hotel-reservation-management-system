package hotel.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.FutureTask;


/**
 * Controller for LoginView.fxml
 *
 * Features:
 * 1. Username and password login
 * 2. Password validation
 * 3. Show Password option
 * 4. Change username/password
 * 5. Password hashing using SHA-256
 * 6. Login information stored in login.properties
 * 7. Multithreaded login verification
 * 8. Opens MainView.fxml after successful login
 */
public class LoginController {

    // ============================================================
    // FXML CONTROLS
    // ============================================================

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private Button loginButton;

    @FXML
    private Button changePasswordButton;


    // ============================================================
    // LOGIN SETTINGS
    // ============================================================

    /*
     * Login information is stored in this file.
     */
    private static final String LOGIN_FILE = "login.properties";


    /*
     * Default login information.
     *
     * Username:
     * admin
     *
     * Password:
     * Admin@123
     */
    private static final String DEFAULT_USERNAME = "admin";

    private static final String DEFAULT_PASSWORD = "Admin@123";


    // ============================================================
    // INITIALIZE
    // ============================================================

    @FXML
    private void initialize() {

        /*
         * Create login.properties if it does not already exist.
         */
        createLoginFileIfNeeded();


        /*
         * Press ENTER in username field
         * -> move to password field.
         */
        usernameField.setOnAction(event ->
                passwordField.requestFocus()
        );


        /*
         * Press ENTER in password field
         * -> perform login.
         */
        passwordField.setOnAction(event ->
                login()
        );
    }


    // ============================================================
    // LOGIN
    // ============================================================

    @FXML
    private void login() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();


        // --------------------------------------------------------
        // Check username
        // --------------------------------------------------------

        if (username.isEmpty()) {

            showWarning(
                    "Missing Username",
                    "Please enter your username."
            );

            usernameField.requestFocus();
            return;
        }


        // --------------------------------------------------------
        // Check password
        // --------------------------------------------------------

        if (password.isEmpty()) {

            showWarning(
                    "Missing Password",
                    "Please enter your password."
            );

            passwordField.requestFocus();
            return;
        }


        // --------------------------------------------------------
        // Check password strength
        // --------------------------------------------------------

        if (!isStrongPassword(password)) {

            showWarning(
                    "Weak Password",
                    getPasswordRequirementMessage()
            );

            passwordField.requestFocus();
            return;
        }


        // --------------------------------------------------------
        // Disable controls while login is being checked
        // --------------------------------------------------------

        usernameField.setDisable(true);
        passwordField.setDisable(true);
        showPasswordCheckBox.setDisable(true);
        loginButton.setDisable(true);
        changePasswordButton.setDisable(true);


        // --------------------------------------------------------
        // MULTITHREADING
        // --------------------------------------------------------

        FutureTask<Boolean> loginTask = new FutureTask<>(
                () -> verifyLogin(username, password)
        ) {

            @Override
            protected void done() {

                try {

                    boolean success = get();


                    /*
                     * JavaFX controls must be changed
                     * on the JavaFX Application Thread.
                     */
                    Platform.runLater(() -> {

                        usernameField.setDisable(false);
                        passwordField.setDisable(false);
                        showPasswordCheckBox.setDisable(false);
                        loginButton.setDisable(false);
                        changePasswordButton.setDisable(false);


                        if (success) {

                            showInformation(
                                    "Login Successful",
                                    "Welcome, " + username + "!"
                            );

                            openMainDashboard();

                        } else {

                            showError(
                                    "Login Failed",
                                    "Invalid username or password."
                            );

                            passwordField.clear();
                            passwordField.requestFocus();
                        }
                    });

                } catch (Exception e) {

                    Platform.runLater(() -> {

                        usernameField.setDisable(false);
                        passwordField.setDisable(false);
                        showPasswordCheckBox.setDisable(false);
                        loginButton.setDisable(false);
                        changePasswordButton.setDisable(false);


                        showError(
                                "Login Error",
                                "An error occurred while checking login."
                        );
                    });
                }
            }
        };


        /*
         * Create a separate thread for login verification.
         */
        Thread loginThread = new Thread(
                loginTask,
                "Login-Verification-Thread"
        );

        loginThread.setDaemon(true);

        loginThread.start();
    }


    // ============================================================
    // VERIFY LOGIN
    // ============================================================

    private boolean verifyLogin(
            String username,
            String password) {

        /*
         * Small delay to demonstrate multithreading.
         */
        try {

            Thread.sleep(500);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return false;
        }


        /*
         * Load saved username/password hash.
         */
        Properties properties = loadLoginProperties();


        String savedUsername =
                properties.getProperty(
                        "username",
                        DEFAULT_USERNAME
                );


        String savedPasswordHash =
                properties.getProperty(
                        "passwordHash",
                        hashPassword(DEFAULT_PASSWORD)
                );


        /*
         * Convert entered password into SHA-256 hash.
         */
        String enteredPasswordHash =
                hashPassword(password);


        /*
         * Compare username and password hash.
         */
        return savedUsername.equals(username)
                && savedPasswordHash.equals(enteredPasswordHash);
    }


    // ============================================================
    // SHOW PASSWORD
    // ============================================================

    @FXML
    private void togglePasswordVisibility() {

        /*
         * Your current FXML contains only a PasswordField.
         *
         * Therefore JavaFX cannot directly change the same
         * PasswordField into a normal TextField.
         *
         * For this project, when "Show Password" is checked,
         * we display the entered password in an information box.
         */

        if (!showPasswordCheckBox.isSelected()) {
            return;
        }


        String password = passwordField.getText();


        if (password.isEmpty()) {

            showInformation(
                    "Password",
                    "No password has been entered."
            );

            showPasswordCheckBox.setSelected(false);

            return;
        }


        showInformation(
                "Password",
                "Your entered password is:\n\n" + password
        );


        /*
         * Return checkbox to unchecked state.
         */
        showPasswordCheckBox.setSelected(false);
    }


    // ============================================================
    // CHANGE USERNAME / PASSWORD
    // ============================================================

    @FXML
    private void changeCredentials() {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(
                "Change Username / Password"
        );

        dialog.setHeaderText(
                "Update Login Credentials"
        );


        // --------------------------------------------------------
        // CURRENT USERNAME
        // --------------------------------------------------------

        Label oldUsernameLabel =
                new Label("Current Username:");

        TextField oldUsernameField =
                new TextField();

        oldUsernameField.setPromptText(
                "Current username"
        );


        // --------------------------------------------------------
        // CURRENT PASSWORD
        // --------------------------------------------------------

        Label oldPasswordLabel =
                new Label("Current Password:");

        PasswordField oldPasswordField =
                new PasswordField();

        oldPasswordField.setPromptText(
                "Current password"
        );


        // --------------------------------------------------------
        // NEW USERNAME
        // --------------------------------------------------------

        Label newUsernameLabel =
                new Label("New Username:");

        TextField newUsernameField =
                new TextField();

        newUsernameField.setPromptText(
                "New username"
        );


        // --------------------------------------------------------
        // NEW PASSWORD
        // --------------------------------------------------------

        Label newPasswordLabel =
                new Label("New Password:");

        PasswordField newPasswordField =
                new PasswordField();

        newPasswordField.setPromptText(
                "New strong password"
        );


        // --------------------------------------------------------
        // CONFIRM PASSWORD
        // --------------------------------------------------------

        Label confirmPasswordLabel =
                new Label("Confirm Password:");

        PasswordField confirmPasswordField =
                new PasswordField();

        confirmPasswordField.setPromptText(
                "Confirm new password"
        );


        // --------------------------------------------------------
        // PASSWORD REQUIREMENTS
        // --------------------------------------------------------

        Label requirementLabel =
                new Label(
                        "Password must contain:\n"
                                + "• At least 8 characters\n"
                                + "• Uppercase letter\n"
                                + "• Lowercase letter\n"
                                + "• Number\n"
                                + "• Special character"
                );


        // --------------------------------------------------------
        // GRIDPANE
        // --------------------------------------------------------

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);

        grid.setPadding(
                new Insets(20)
        );


        // Current username
        grid.add(
                oldUsernameLabel,
                0,
                0
        );

        grid.add(
                oldUsernameField,
                1,
                0
        );


        // Current password
        grid.add(
                oldPasswordLabel,
                0,
                1
        );

        grid.add(
                oldPasswordField,
                1,
                1
        );


        // New username
        grid.add(
                newUsernameLabel,
                0,
                2
        );

        grid.add(
                newUsernameField,
                1,
                2
        );


        // New password
        grid.add(
                newPasswordLabel,
                0,
                3
        );

        grid.add(
                newPasswordField,
                1,
                3
        );


        // Confirm password
        grid.add(
                confirmPasswordLabel,
                0,
                4
        );

        grid.add(
                confirmPasswordField,
                1,
                4
        );


        // Password requirements
        grid.add(
                requirementLabel,
                1,
                5
        );


        // --------------------------------------------------------
        // DIALOG BUTTONS
        // --------------------------------------------------------

        ButtonType updateButton =
                new ButtonType(
                        "Update",
                        ButtonBar.ButtonData.OK_DONE
                );


        ButtonType cancelButton =
                new ButtonType(
                        "Cancel",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        updateButton,
                        cancelButton
                );


        DialogPane dialogPane =
                dialog.getDialogPane();

        dialogPane.setContent(grid);


        // --------------------------------------------------------
        // DISABLE UPDATE BUTTON INITIALLY
        // --------------------------------------------------------

        javafx.scene.Node updateNode =
                dialogPane.lookupButton(updateButton);

        updateNode.setDisable(true);


        // --------------------------------------------------------
        // VALIDATE DIALOG FIELDS
        // --------------------------------------------------------

        Runnable validateFields = () -> {

            boolean valid =
                    !oldUsernameField.getText()
                            .trim()
                            .isEmpty()

                            && !oldPasswordField.getText()
                            .isEmpty()

                            && !newUsernameField.getText()
                            .trim()
                            .isEmpty()

                            && !newPasswordField.getText()
                            .isEmpty()

                            && !confirmPasswordField.getText()
                            .isEmpty();


            updateNode.setDisable(!valid);
        };


        oldUsernameField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                validateFields.run()
                );


        oldPasswordField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                validateFields.run()
                );


        newUsernameField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                validateFields.run()
                );


        newPasswordField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                validateFields.run()
                );


        confirmPasswordField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                validateFields.run()
                );


        // --------------------------------------------------------
        // SHOW DIALOG
        // --------------------------------------------------------

        Optional<ButtonType> result =
                dialog.showAndWait();


        if (result.isEmpty()
                || result.get() != updateButton) {

            return;
        }


        // --------------------------------------------------------
        // READ VALUES
        // --------------------------------------------------------

        String oldUsername =
                oldUsernameField.getText().trim();

        String oldPassword =
                oldPasswordField.getText();

        String newUsername =
                newUsernameField.getText().trim();

        String newPassword =
                newPasswordField.getText();

        String confirmPassword =
                confirmPasswordField.getText();


        // --------------------------------------------------------
        // CHECK CURRENT CREDENTIALS
        // --------------------------------------------------------

        if (!verifyLogin(
                oldUsername,
                oldPassword)) {

            showError(
                    "Invalid Credentials",
                    "Current username or password is incorrect."
            );

            return;
        }


        // --------------------------------------------------------
        // CHECK NEW USERNAME
        // --------------------------------------------------------

        if (newUsername.length() < 3) {

            showWarning(
                    "Invalid Username",
                    "Username must contain at least 3 characters."
            );

            return;
        }


        // --------------------------------------------------------
        // CHECK NEW PASSWORD
        // --------------------------------------------------------

        if (!isStrongPassword(newPassword)) {

            showWarning(
                    "Weak Password",
                    getPasswordRequirementMessage()
            );

            return;
        }


        // --------------------------------------------------------
        // CONFIRM NEW PASSWORD
        // --------------------------------------------------------

        if (!newPassword.equals(confirmPassword)) {

            showError(
                    "Password Mismatch",
                    "New password and confirm password do not match."
            );

            return;
        }


        // --------------------------------------------------------
        // SAVE NEW CREDENTIALS
        // --------------------------------------------------------

        saveCredentials(
                newUsername,
                newPassword
        );


        showInformation(
                "Credentials Updated",
                "Username and password have been changed successfully."
        );
    }


    // ============================================================
    // STRONG PASSWORD VALIDATION
    // ============================================================

    private boolean isStrongPassword(
            String password) {

        if (password == null
                || password.length() < 8) {

            return false;
        }


        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;


        for (char ch : password.toCharArray()) {

            if (Character.isUpperCase(ch)) {

                hasUppercase = true;

            } else if (Character.isLowerCase(ch)) {

                hasLowercase = true;

            } else if (Character.isDigit(ch)) {

                hasDigit = true;

            } else {

                hasSpecial = true;
            }
        }


        return hasUppercase
                && hasLowercase
                && hasDigit
                && hasSpecial;
    }


    // ============================================================
    // PASSWORD REQUIREMENT MESSAGE
    // ============================================================

    private String getPasswordRequirementMessage() {

        return """
                Password is too weak.

                Your password must contain:

                • At least 8 characters
                • At least one uppercase letter (A-Z)
                • At least one lowercase letter (a-z)
                • At least one number (0-9)
                • At least one special character (!, @, #, $, etc.)

                Example:
                Admin@123
                """;
    }


    // ============================================================
    // CREATE LOGIN FILE
    // ============================================================

    private void createLoginFileIfNeeded() {

        Properties properties =
                new Properties();


        try {

            /*
             * Try to open existing login.properties.
             */
            try (FileInputStream input =
                         new FileInputStream(LOGIN_FILE)) {

                properties.load(input);

            } catch (IOException e) {

                /*
                 * File does not exist.
                 * Create default credentials.
                 */

                properties.setProperty(
                        "username",
                        DEFAULT_USERNAME
                );


                properties.setProperty(
                        "passwordHash",
                        hashPassword(DEFAULT_PASSWORD)
                );


                try (FileOutputStream output =
                             new FileOutputStream(LOGIN_FILE)) {

                    properties.store(
                            output,
                            "Hotel Management Login Credentials"
                    );
                }
            }

        } catch (IOException e) {

            showError(
                    "File Error",
                    "Could not create login credential file."
            );
        }
    }


    // ============================================================
    // LOAD LOGIN PROPERTIES
    // ============================================================

    private Properties loadLoginProperties() {

        Properties properties =
                new Properties();


        try (FileInputStream input =
                     new FileInputStream(LOGIN_FILE)) {

            properties.load(input);

        } catch (IOException e) {

            /*
             * If the file cannot be read,
             * use default credentials.
             */

            properties.setProperty(
                    "username",
                    DEFAULT_USERNAME
            );


            properties.setProperty(
                    "passwordHash",
                    hashPassword(DEFAULT_PASSWORD)
            );
        }


        return properties;
    }


    // ============================================================
    // SAVE CREDENTIALS
    // ============================================================

    private void saveCredentials(
            String username,
            String password) {

        Properties properties =
                new Properties();


        properties.setProperty(
                "username",
                username
        );


        properties.setProperty(
                "passwordHash",
                hashPassword(password)
        );


        try (FileOutputStream output =
                     new FileOutputStream(LOGIN_FILE)) {

            properties.store(
                    output,
                    "Hotel Management Login Credentials"
            );

        } catch (IOException e) {

            showError(
                    "Save Error",
                    "Could not save the new login credentials."
            );
        }
    }


    // ============================================================
    // PASSWORD HASHING
    // ============================================================

    private String hashPassword(
            String password) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );


            byte[] hash =
                    digest.digest(
                            password.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );


            StringBuilder hexString =
                    new StringBuilder();


            for (byte b : hash) {

                String hex =
                        Integer.toHexString(
                                0xff & b
                        );


                if (hex.length() == 1) {

                    hexString.append('0');
                }


                hexString.append(hex);
            }


            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(
                    "SHA-256 algorithm not available.",
                    e
            );
        }
    }


    // ============================================================
    // OPEN MAIN DASHBOARD
    // ============================================================

    private void openMainDashboard() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource(
                                            "/view/MainView.fxml"
                                    )
                    );


            Scene scene =
                    new Scene(
                            loader.load()
                    );


            Stage mainStage =
                    new Stage();


            mainStage.setTitle(
                    "Hotel Reservation and Management System"
            );


            mainStage.setScene(scene);


            /*
             * Open the Main Dashboard maximized.
             *
             * This replaces the previous fixed
             * 600 x 600 window size.
             */
            mainStage.setMaximized(true);


            mainStage.show();


            /*
             * Close Login window.
             */
            Stage loginStage =
                    (Stage) usernameField
                            .getScene()
                            .getWindow();

            loginStage.close();


        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Error",
                    "Could not open Main Dashboard."
            );
        }
    }


    // ============================================================
    // INFORMATION ALERT
    // ============================================================

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


    // ============================================================
    // WARNING ALERT
    // ============================================================

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


    // ============================================================
    // ERROR ALERT
    // ============================================================

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