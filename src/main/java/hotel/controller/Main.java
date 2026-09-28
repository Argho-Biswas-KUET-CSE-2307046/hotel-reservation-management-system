package hotel.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // =====================================================
        // LOAD LOGIN PAGE
        // =====================================================

        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource(
                        "/view/LoginView.fxml"
                )
        );

        Scene scene = new Scene(
                loader.load()
        );


        // =====================================================
        // WINDOW SETTINGS
        // =====================================================

        stage.setTitle(
                "Hotel Reservation and Management System"
        );

        stage.setScene(scene);


        /*
         * Allow the Login window to be resized.
         *
         * The actual responsive layout of the Login page
         * will be handled by LoginView.fxml.
         */
        stage.setResizable(true);


        /*
         * Minimum window size.
         *
         * This prevents the Login interface from becoming
         * too small and causing its controls to overlap.
         */
        stage.setMinWidth(500);

        stage.setMinHeight(500);


        /*
         * Initial Login window size.
         *
         * This is only the starting size.
         * The user can still resize the window.
         */
        stage.setWidth(600);

        stage.setHeight(600);


        /*
         * Open the Login window in the center of the screen.
         */
        stage.centerOnScreen();


        // =====================================================
        // SHOW WINDOW
        // =====================================================

        stage.show();
    }


    // =========================================================
    // APPLICATION ENTRY POINT
    // =========================================================

    public static void main(String[] args) {

        launch(args);
    }
}