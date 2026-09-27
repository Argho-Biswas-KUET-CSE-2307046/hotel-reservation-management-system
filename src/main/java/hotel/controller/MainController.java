package hotel.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

    // =========================
    // RESPONSIVE BUTTONS
    // =========================

    @FXML
    private Button refreshDataButton;

    @FXML
    private Button logoutButton;


    // =========================
    // INITIALIZE
    // =========================

    @FXML
    private void initialize() {

        // Make Refresh Data button responsive
        refreshDataButton.sceneProperty().addListener(
                (observable, oldScene, newScene) -> {

                    if (newScene != null
                            && !refreshDataButton.prefWidthProperty().isBound()) {

                        // Width = 15% of window width
                        refreshDataButton.prefWidthProperty().bind(
                                newScene.widthProperty().multiply(0.15)
                        );

                        // Height = 8% of window height
                        refreshDataButton.prefHeightProperty().bind(
                                newScene.heightProperty().multiply(0.08)
                        );
                    }
                }
        );


        // Make Logout button responsive
        logoutButton.sceneProperty().addListener(
                (observable, oldScene, newScene) -> {

                    if (newScene != null
                            && !logoutButton.prefWidthProperty().isBound()) {

                        // Width = 12% of window width
                        logoutButton.prefWidthProperty().bind(
                                newScene.widthProperty().multiply(0.12)
                        );

                        // Height = 8% of window height
                        logoutButton.prefHeightProperty().bind(
                                newScene.heightProperty().multiply(0.08)
                        );
                    }
                }
        );
    }


    // =========================
    // ROOM MANAGEMENT
    // =========================

    @FXML
    private void openRoomManagement() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource(
                            "/view/RoomView.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();

            stage.setTitle("Room Management");
            stage.setScene(scene);

            stage.setWidth(900);
            stage.setHeight(600);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Could not open Room Management."
            );
        }
    }


    // =========================
    // CUSTOMER MANAGEMENT
    // =========================

    @FXML
    private void openCustomerManagement() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource(
                            "/view/CustomerView.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();

            stage.setTitle("Customer Management");
            stage.setScene(scene);

            stage.setWidth(900);
            stage.setHeight(650);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Could not open Customer Management."
            );
        }
    }


    // =========================
    // BOOKING MANAGEMENT
    // =========================

    @FXML
    private void openBooking() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource(
                            "/view/BookingView.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();

            stage.setTitle("Booking Management");
            stage.setScene(scene);

            stage.setWidth(900);
            stage.setHeight(700);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Could not open Booking Management."
            );
        }
    }


    // =========================
    // BOOKING HISTORY
    // =========================

    @FXML
    private void openBookingHistory() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource(
                            "/view/BookingHistoryView.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();

            stage.setTitle("Booking History");
            stage.setScene(scene);

            stage.setWidth(900);
            stage.setHeight(600);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Could not open Booking History."
            );
        }
    }


    // =========================
    // FINANCE & REPORTS
    // =========================

    @FXML
    private void openFinance() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource(
                            "/view/FinanceView.fxml"
                    )
            );

            Scene scene = new Scene(
                    loader.load()
            );

            Stage stage = new Stage();

            stage.setTitle(
                    "Finance & Reports"
            );

            stage.setScene(
                    scene
            );

            stage.setWidth(
                    1200
            );

            stage.setHeight(
                    800
            );

            stage.setMinWidth(
                    1000
            );

            stage.setMinHeight(
                    650
            );

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Finance Error",
                    "Could not open Finance & Reports."
            );
        }
    }


    // =========================
    // WEATHER INFORMATION
    // =========================

    @FXML
    private void openWeather() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource(
                            "/view/WeatherView.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();

            stage.setTitle("Weather Information");
            stage.setScene(scene);

            stage.setWidth(1000);
            stage.setHeight(650);

            stage.setMinWidth(800);
            stage.setMinHeight(550);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Weather Error",
                    "Could not open Weather Information."
            );
        }
    }


    // =========================
    // MENU BAR - NEW
    // =========================

    @FXML
    private void newAction() {

        openBooking();
    }


    // =========================
    // MENU BAR - OPEN
    // =========================

    @FXML
    private void openAction() {

        showMessage(
                "Open",
                "Open option selected."
        );
    }


    // =========================
    // REFRESH DATA
    // =========================

    @FXML
    private void refreshData() {

        showMessage(
                "Refresh Data",
                "Dashboard data has been refreshed successfully."
        );
    }


    // =========================
    // LOGOUT
    // =========================

    @FXML
    private void logout(ActionEvent event) {

        try {

            // Get current dashboard window
            Stage currentStage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();


            // Load Login page
            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource(
                            "/view/LoginView.fxml"
                    )
            );

            Scene loginScene =
                    new Scene(loader.load());


            // Close dashboard
            currentStage.close();


            // Create Login window
            Stage loginStage = new Stage();

            loginStage.setTitle(
                    "Hotel Reservation and Management System"
            );

            loginStage.setScene(loginScene);

            loginStage.setResizable(false);

            loginStage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Logout Error",
                    "Could not return to the Login page."
            );
        }
    }


    // =========================
    // EXIT APPLICATION
    // =========================

    @FXML
    private void exitApplication() {

        System.exit(0);
    }


    // =========================
    // COMMON MESSAGE
    // =========================

    private void showMessage(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}