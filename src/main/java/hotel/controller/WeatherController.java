package hotel.controller;

import hotel.animation.WeatherAnimation;
import hotel.model.ForecastDay;
import hotel.model.Weather;
import hotel.service.WeatherService;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.Future;


// =========================================================
// WEATHER CONTROLLER
// =========================================================

public class WeatherController {


    // =========================================================
    // FXML CONTROLS
    // =========================================================

    @FXML
    private TextField cityField;

    @FXML
    private Button refreshWeatherButton;

    @FXML
    private Label cityLabel;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private Label conditionLabel;

    @FXML
    private Label statusLabel;


    // =========================================================
    // 7-DAY FORECAST CONTAINER
    // =========================================================

    @FXML
    private HBox forecastContainer;


    // =========================================================
    // WEATHER SERVICE
    // =========================================================

    private final WeatherService weatherService =
            new WeatherService();


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        // ---------------------------------------------------------
        // DEFAULT CITY
        // ---------------------------------------------------------

        cityField.setText("Khulna");


        // ---------------------------------------------------------
        // INITIAL STATUS
        // ---------------------------------------------------------

        statusLabel.setText(
                "Loading weather information..."
        );


        // ---------------------------------------------------------
        // INITIAL FORECAST MESSAGE
        // ---------------------------------------------------------

        if (forecastContainer != null) {

            forecastContainer.getChildren().clear();

            Label loadingLabel =
                    new Label(
                            "Loading 7-day forecast..."
                    );

            loadingLabel.setStyle(
                    "-fx-text-fill: #64748B;" +
                            "-fx-font-size: 14px;"
            );

            forecastContainer.getChildren().add(
                    loadingLabel
            );
        }


        // =========================================================
        // AUTOMATICALLY LOAD WEATHER
        // =========================================================

        Platform.runLater(() -> {

            refreshWeather();

        });
    }


    // =========================================================
    // REFRESH WEATHER
    // =========================================================

    @FXML
    private void refreshWeather() {

        // ---------------------------------------------------------
        // GET CITY NAME
        // ---------------------------------------------------------

        String city =
                cityField.getText().trim();


        // =========================================================
        // INPUT VALIDATION
        // =========================================================

        if (city.isEmpty()) {

            showError(
                    "Input Error",
                    "Please enter a city name."
            );

            return;
        }


        // =========================================================
        // DISABLE BUTTON
        // =========================================================

        refreshWeatherButton.setDisable(true);


        // =========================================================
        // STATUS
        // =========================================================

        statusLabel.setText(
                "Fetching current weather and 7-day forecast..."
        );


        // =========================================================
        // CLEAR OLD FORECAST
        // =========================================================

        if (forecastContainer != null) {

            forecastContainer.getChildren().clear();

            Label loadingLabel =
                    new Label(
                            "Loading 7-day forecast..."
                    );

            loadingLabel.setStyle(
                    "-fx-text-fill: #64748B;" +
                            "-fx-font-size: 14px;"
            );

            forecastContainer.getChildren().add(
                    loadingLabel
            );
        }


        // =========================================================
        // CURRENT WEATHER
        // =========================================================

        Future<Weather> weatherFuture =
                weatherService.getWeatherAsync(
                        city
                );


        // =========================================================
        // 7-DAY FORECAST
        // =========================================================

        Future<List<ForecastDay>> forecastFuture =
                weatherService.get7DayForecastAsync(
                        city
                );


        // =========================================================
        // BACKGROUND RESULT THREAD
        // =========================================================

        Thread resultThread =
                new Thread(() -> {

                    try {

                        // -------------------------------------------------
                        // CURRENT WEATHER
                        // -------------------------------------------------

                        Weather weather =
                                weatherFuture.get();


                        // -------------------------------------------------
                        // 7-DAY FORECAST
                        // -------------------------------------------------

                        List<ForecastDay> forecastList =
                                forecastFuture.get();


                        // =================================================
                        // UPDATE JAVAFX UI
                        // =================================================

                        Platform.runLater(() -> {

                            // -------------------------------------------------
                            // CURRENT WEATHER
                            // -------------------------------------------------

                            updateCurrentWeather(
                                    weather
                            );


                            // -------------------------------------------------
                            // 7-DAY FORECAST
                            // -------------------------------------------------

                            updateForecast(
                                    forecastList
                            );


                            // -------------------------------------------------
                            // STATUS
                            // -------------------------------------------------

                            statusLabel.setText(
                                    "Weather data updated successfully."
                            );


                            // -------------------------------------------------
                            // ENABLE BUTTON
                            // -------------------------------------------------

                            refreshWeatherButton.setDisable(
                                    false
                            );

                        });

                    } catch (Exception e) {

                        e.printStackTrace();


                        // =================================================
                        // ERROR HANDLING
                        // =================================================

                        Platform.runLater(() -> {

                            statusLabel.setText(
                                    "Unable to load weather data."
                            );


                            refreshWeatherButton.setDisable(
                                    false
                            );


                            if (forecastContainer != null) {

                                forecastContainer
                                        .getChildren()
                                        .clear();

                                Label errorLabel =
                                        new Label(
                                                "Unable to load 7-day forecast."
                                        );

                                errorLabel.setStyle(
                                        "-fx-text-fill: #DC2626;" +
                                                "-fx-font-size: 14px;"
                                );

                                forecastContainer
                                        .getChildren()
                                        .add(
                                                errorLabel
                                        );
                            }


                            showError(
                                    "Weather Error",
                                    "Could not retrieve weather information."
                            );

                        });

                    }

                });


        // =========================================================
        // DAEMON THREAD
        // =========================================================

        resultThread.setDaemon(true);


        // =========================================================
        // START THREAD
        // =========================================================

        resultThread.start();
    }


    // =========================================================
    // UPDATE CURRENT WEATHER
    // =========================================================

    private void updateCurrentWeather(
            Weather weather) {

        cityLabel.setText(
                weather.getCity()
        );


        temperatureLabel.setText(
                String.format(
                        "%.1f °C",
                        weather.getTemperature()
                )
        );


        humidityLabel.setText(
                weather.getHumidity()
                        + "%"
        );


        conditionLabel.setText(
                weather.getCondition()
        );
    }


    // =========================================================
    // UPDATE 7-DAY FORECAST
    // =========================================================

    private void updateForecast(
            List<ForecastDay> forecastList) {

        if (forecastContainer == null) {

            return;
        }


        // ---------------------------------------------------------
        // CLEAR OLD CARDS
        // ---------------------------------------------------------

        forecastContainer
                .getChildren()
                .clear();


        // ---------------------------------------------------------
        // CHECK FORECAST DATA
        // ---------------------------------------------------------

        if (forecastList == null
                || forecastList.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "No forecast data available."
                    );

            emptyLabel.setStyle(
                    "-fx-text-fill: #64748B;" +
                            "-fx-font-size: 14px;"
            );

            forecastContainer
                    .getChildren()
                    .add(
                            emptyLabel
                    );

            return;
        }


        // =========================================================
        // CREATE FORECAST CARDS
        // =========================================================

        for (int i = 0;
             i < forecastList.size();
             i++) {

            ForecastDay forecastDay =
                    forecastList.get(i);


            VBox card =
                    createForecastCard(
                            forecastDay,
                            i
                    );


            forecastContainer
                    .getChildren()
                    .add(
                            card
                    );
        }
    }


    // =========================================================
    // CREATE FORECAST CARD
    // =========================================================

    private VBox createForecastCard(
            ForecastDay forecastDay,
            int index) {


        // =========================================================
        // CARD
        // =========================================================

        VBox card =
                new VBox();


        // ---------------------------------------------------------
        // CARD WIDTH
        // ---------------------------------------------------------

        card.setPrefWidth(
                145
        );

        card.setMinWidth(
                145
        );

        card.setMaxWidth(
                180
        );


        // ---------------------------------------------------------
        // CARD HEIGHT
        // ---------------------------------------------------------

        card.setPrefHeight(
                250
        );

        card.setMinHeight(
                250
        );

        card.setMaxHeight(
                260
        );


        // ---------------------------------------------------------
        // SPACING
        // ---------------------------------------------------------

        card.setSpacing(
                8
        );


        // ---------------------------------------------------------
        // ALIGNMENT
        // ---------------------------------------------------------

        card.setAlignment(
                Pos.TOP_CENTER
        );


        // ---------------------------------------------------------
        // PADDING
        // ---------------------------------------------------------

        card.setPadding(
                new Insets(
                        15
                )
        );


        // ---------------------------------------------------------
        // CARD STYLE
        // ---------------------------------------------------------

        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #D7DEE8;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );


        // =========================================================
        // DAY NAME
        // =========================================================

        Label dayLabel =
                new Label(
                        index == 0
                                ? "TODAY"
                                : forecastDay.getDayName()
                );


        dayLabel.setStyle(
                "-fx-text-fill: #1E3A8A;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );


        // =========================================================
        // DATE
        // =========================================================

        Label dateLabel =
                new Label(
                        forecastDay.getDate()
                );


        dateLabel.setStyle(
                "-fx-text-fill: #64748B;" +
                        "-fx-font-size: 12px;"
        );


        // =========================================================
        // WEATHER ANIMATION
        // =========================================================
        //
        // This replaces the old static emoji icon.
        //
        // The animation is automatically selected according
        // to the weather condition.
        //
        // =========================================================

        StackPane weatherAnimation =
                WeatherAnimation.createAnimation(
                        forecastDay.getCondition()
                );


        weatherAnimation.setPrefSize(
                120,
                65
        );

        weatherAnimation.setMinSize(
                120,
                65
        );

        weatherAnimation.setMaxSize(
                120,
                65
        );


        // =========================================================
        // CONDITION
        // =========================================================

        Label forecastConditionLabel =
                new Label(
                        forecastDay.getCondition()
                );


        forecastConditionLabel.setWrapText(
                true
        );


        forecastConditionLabel.setAlignment(
                Pos.CENTER
        );


        forecastConditionLabel.setMaxWidth(
                125
        );


        forecastConditionLabel.setStyle(
                "-fx-text-fill: #334155;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );


        // =========================================================
        // MAXIMUM TEMPERATURE
        // =========================================================

        Label maxTemperatureLabel =
                new Label(
                        String.format(
                                "High: %.1f °C",
                                forecastDay
                                        .getMaxTemperature()
                        )
                );


        maxTemperatureLabel.setStyle(
                "-fx-text-fill: #DC2626;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );


        // =========================================================
        // MINIMUM TEMPERATURE
        // =========================================================

        Label minTemperatureLabel =
                new Label(
                        String.format(
                                "Low: %.1f °C",
                                forecastDay
                                        .getMinTemperature()
                        )
                );


        minTemperatureLabel.setStyle(
                "-fx-text-fill: #2563EB;" +
                        "-fx-font-size: 13px;"
        );


        // =========================================================
        // PRECIPITATION
        // =========================================================

        Label precipitationLabel =
                new Label(
                        String.format(
                                "Rain: %.1f mm",
                                forecastDay
                                        .getPrecipitation()
                        )
                );


        precipitationLabel.setStyle(
                "-fx-text-fill: #64748B;" +
                        "-fx-font-size: 11px;"
        );


        // =========================================================
        // SNOWFALL
        // =========================================================

        Label snowfallLabel =
                new Label(
                        String.format(
                                "Snow: %.1f cm",
                                forecastDay
                                        .getSnowfall()
                        )
                );


        snowfallLabel.setStyle(
                "-fx-text-fill: #64748B;" +
                        "-fx-font-size: 11px;"
        );


        // =========================================================
        // ADD EVERYTHING TO CARD
        // =========================================================

        card.getChildren().addAll(

                dayLabel,

                dateLabel,

                weatherAnimation,

                forecastConditionLabel,

                maxTemperatureLabel,

                minTemperatureLabel,

                precipitationLabel,

                snowfallLabel
        );


        return card;
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


        alert.setTitle(
                title
        );


        alert.setHeaderText(
                null
        );


        alert.setContentText(
                message
        );


        alert.showAndWait();
    }


    // =========================================================
    // BACK TO MAIN DASHBOARD
    // =========================================================

    @FXML
    private void backToMain() {

        // ---------------------------------------------------------
        // STOP WEATHER SERVICE THREAD POOL
        // ---------------------------------------------------------

        weatherService.shutdown();


        // ---------------------------------------------------------
        // GET CURRENT WEATHER WINDOW
        // ---------------------------------------------------------

        Stage stage =
                (Stage) cityField
                        .getScene()
                        .getWindow();


        // ---------------------------------------------------------
        // CLOSE WEATHER WINDOW
        // ---------------------------------------------------------

        stage.close();
    }
}