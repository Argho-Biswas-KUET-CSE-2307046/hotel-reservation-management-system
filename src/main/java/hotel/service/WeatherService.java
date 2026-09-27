package hotel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hotel.model.ForecastDay;
import hotel.model.Weather;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


// =========================================================
// WEATHER SERVICE
// =========================================================

public class WeatherService {

    // =========================================================
    // HTTP CLIENT
    // =========================================================

    private final HttpClient httpClient;


    // =========================================================
    // JSON OBJECT MAPPER
    // =========================================================

    private final ObjectMapper objectMapper;


    // =========================================================
    // THREAD POOL
    // =========================================================

    private final ExecutorService executorService =
            Executors.newFixedThreadPool(2);


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public WeatherService() {

        httpClient =
                HttpClient.newHttpClient();

        objectMapper =
                new ObjectMapper();
    }


    // =========================================================
    // GET CURRENT WEATHER
    // =========================================================
    //
    // This method keeps the existing current-weather
    // functionality.
    //
    // It returns:
    //      City
    //      Temperature
    //      Humidity
    //      Condition
    //
    // =========================================================

    public Weather getWeather(String city) throws Exception {

        // =====================================================
        // STEP 1: FIND CITY COORDINATES
        // =====================================================

        String encodedCity =
                URLEncoder.encode(
                        city,
                        StandardCharsets.UTF_8
                );


        String geocodingUrl =
                "https://geocoding-api.open-meteo.com/v1/search"
                        + "?name=" + encodedCity
                        + "&count=1"
                        + "&language=en"
                        + "&format=json";


        HttpRequest geocodingRequest =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        geocodingUrl
                                )
                        )
                        .GET()
                        .build();


        // =====================================================
        // STEP 2: SEND GEOCODING REQUEST
        // =====================================================

        HttpResponse<String> geocodingResponse =
                httpClient.send(
                        geocodingRequest,
                        HttpResponse.BodyHandlers.ofString()
                );


        if (geocodingResponse.statusCode() != 200) {

            throw new IOException(
                    "Geocoding API request failed. HTTP status: "
                            + geocodingResponse.statusCode()
            );
        }


        // =====================================================
        // STEP 3: PARSE GEOCODING JSON
        // =====================================================

        JsonNode geocodingJson =
                objectMapper.readTree(
                        geocodingResponse.body()
                );


        JsonNode results =
                geocodingJson.get("results");


        if (results == null || results.isEmpty()) {

            throw new IOException(
                    "City not found: " + city
            );
        }


        JsonNode location =
                results.get(0);


        double latitude =
                location
                        .get("latitude")
                        .asDouble();


        double longitude =
                location
                        .get("longitude")
                        .asDouble();


        String locationName =
                location
                        .get("name")
                        .asText();


        // =====================================================
        // STEP 4: BUILD CURRENT WEATHER API URL
        // =====================================================

        String weatherUrl =
                "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + latitude
                        + "&longitude=" + longitude
                        + "&current="
                        + "temperature_2m,"
                        + "relative_humidity_2m,"
                        + "weather_code"
                        + "&temperature_unit=celsius";


        HttpRequest weatherRequest =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        weatherUrl
                                )
                        )
                        .GET()
                        .build();


        // =====================================================
        // STEP 5: SEND WEATHER REQUEST
        // =====================================================

        HttpResponse<String> weatherResponse =
                httpClient.send(
                        weatherRequest,
                        HttpResponse.BodyHandlers.ofString()
                );


        if (weatherResponse.statusCode() != 200) {

            throw new IOException(
                    "Weather API request failed. HTTP status: "
                            + weatherResponse.statusCode()
            );
        }


        // =====================================================
        // STEP 6: PARSE WEATHER JSON
        // =====================================================

        JsonNode weatherJson =
                objectMapper.readTree(
                        weatherResponse.body()
                );


        JsonNode current =
                weatherJson.get("current");


        if (current == null) {

            throw new IOException(
                    "Current weather data was not found."
            );
        }


        // =====================================================
        // STEP 7: GET CURRENT TEMPERATURE
        // =====================================================

        double temperature =
                current
                        .get("temperature_2m")
                        .asDouble();


        // =====================================================
        // STEP 8: GET CURRENT HUMIDITY
        // =====================================================

        int humidity =
                current
                        .get("relative_humidity_2m")
                        .asInt();


        // =====================================================
        // STEP 9: GET CURRENT WEATHER CODE
        // =====================================================

        int weatherCode =
                current
                        .get("weather_code")
                        .asInt();


        // =====================================================
        // STEP 10: CONVERT WEATHER CODE
        // =====================================================

        String condition =
                getWeatherCondition(
                        weatherCode
                );


        // =====================================================
        // STEP 11: CREATE WEATHER OBJECT
        // =====================================================

        return new Weather(
                locationName,
                temperature,
                humidity,
                condition
        );
    }


    // =========================================================
    // GET 7-DAY FORECAST
    // =========================================================
    //
    // This method retrieves:
    //
    // 1. Date
    // 2. Maximum temperature
    // 3. Minimum temperature
    // 4. Weather code
    // 5. Precipitation
    // 6. Snowfall
    //
    // for 7 days.
    //
    // =========================================================

    public List<ForecastDay> get7DayForecast(
            String city) throws Exception {

        // =====================================================
        // STEP 1: FIND CITY COORDINATES
        // =====================================================

        String encodedCity =
                URLEncoder.encode(
                        city,
                        StandardCharsets.UTF_8
                );


        String geocodingUrl =
                "https://geocoding-api.open-meteo.com/v1/search"
                        + "?name=" + encodedCity
                        + "&count=1"
                        + "&language=en"
                        + "&format=json";


        HttpRequest geocodingRequest =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        geocodingUrl
                                )
                        )
                        .GET()
                        .build();


        // =====================================================
        // STEP 2: SEND GEOCODING REQUEST
        // =====================================================

        HttpResponse<String> geocodingResponse =
                httpClient.send(
                        geocodingRequest,
                        HttpResponse.BodyHandlers.ofString()
                );


        if (geocodingResponse.statusCode() != 200) {

            throw new IOException(
                    "Geocoding API request failed. HTTP status: "
                            + geocodingResponse.statusCode()
            );
        }


        // =====================================================
        // STEP 3: PARSE GEOCODING JSON
        // =====================================================

        JsonNode geocodingJson =
                objectMapper.readTree(
                        geocodingResponse.body()
                );


        JsonNode results =
                geocodingJson.get("results");


        if (results == null || results.isEmpty()) {

            throw new IOException(
                    "City not found: " + city
            );
        }


        JsonNode location =
                results.get(0);


        double latitude =
                location
                        .get("latitude")
                        .asDouble();


        double longitude =
                location
                        .get("longitude")
                        .asDouble();


        // =====================================================
        // STEP 4: BUILD 7-DAY WEATHER API URL
        // =====================================================

        String weatherUrl =
                "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + latitude
                        + "&longitude=" + longitude

                        + "&daily="
                        + "weather_code,"
                        + "temperature_2m_max,"
                        + "temperature_2m_min,"
                        + "precipitation_sum,"
                        + "snowfall_sum"

                        + "&temperature_unit=celsius"

                        + "&forecast_days=7"

                        + "&timezone=auto";


        HttpRequest weatherRequest =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        weatherUrl
                                )
                        )
                        .GET()
                        .build();


        // =====================================================
        // STEP 5: SEND 7-DAY WEATHER REQUEST
        // =====================================================

        HttpResponse<String> weatherResponse =
                httpClient.send(
                        weatherRequest,
                        HttpResponse.BodyHandlers.ofString()
                );


        if (weatherResponse.statusCode() != 200) {

            throw new IOException(
                    "7-day weather request failed. HTTP status: "
                            + weatherResponse.statusCode()
            );
        }


        // =====================================================
        // STEP 6: PARSE WEATHER JSON
        // =====================================================

        JsonNode weatherJson =
                objectMapper.readTree(
                        weatherResponse.body()
                );


        JsonNode daily =
                weatherJson.get("daily");


        if (daily == null) {

            throw new IOException(
                    "7-day forecast data was not found."
            );
        }


        // =====================================================
        // STEP 7: GET DAILY ARRAYS
        // =====================================================

        JsonNode dates =
                daily.get("time");


        JsonNode maxTemperatures =
                daily.get("temperature_2m_max");


        JsonNode minTemperatures =
                daily.get("temperature_2m_min");


        JsonNode weatherCodes =
                daily.get("weather_code");


        JsonNode precipitation =
                daily.get("precipitation_sum");


        JsonNode snowfall =
                daily.get("snowfall_sum");


        if (dates == null
                || maxTemperatures == null
                || minTemperatures == null
                || weatherCodes == null) {

            throw new IOException(
                    "Required 7-day forecast fields are missing."
            );
        }


        // =====================================================
        // STEP 8: CREATE FORECAST LIST
        // =====================================================

        List<ForecastDay> forecastList =
                new ArrayList<>();


        // =====================================================
        // STEP 9: PROCESS 7 DAYS
        // =====================================================

        int numberOfDays =
                Math.min(
                        7,
                        dates.size()
                );


        for (int i = 0; i < numberOfDays; i++) {

            // -------------------------------------------------
            // DATE
            // -------------------------------------------------

            String date =
                    dates
                            .get(i)
                            .asText();


            // -------------------------------------------------
            // CONVERT DATE TO DAY NAME
            // -------------------------------------------------

            LocalDate localDate =
                    LocalDate.parse(
                            date
                    );


            DayOfWeek dayOfWeek =
                    localDate.getDayOfWeek();


            String dayName =
                    getDayName(
                            dayOfWeek
                    );


            // -------------------------------------------------
            // MAXIMUM TEMPERATURE
            // -------------------------------------------------

            double maxTemperature =
                    maxTemperatures
                            .get(i)
                            .asDouble();


            // -------------------------------------------------
            // MINIMUM TEMPERATURE
            // -------------------------------------------------

            double minTemperature =
                    minTemperatures
                            .get(i)
                            .asDouble();


            // -------------------------------------------------
            // WEATHER CODE
            // -------------------------------------------------

            int dailyWeatherCode =
                    weatherCodes
                            .get(i)
                            .asInt();


            // -------------------------------------------------
            // WEATHER CONDITION
            // -------------------------------------------------

            String condition =
                    getWeatherCondition(
                            dailyWeatherCode
                    );


            // -------------------------------------------------
            // PRECIPITATION
            // -------------------------------------------------

            double precipitationValue =
                    0.0;


            if (precipitation != null
                    && !precipitation.get(i).isNull()) {

                precipitationValue =
                        precipitation
                                .get(i)
                                .asDouble();
            }


            // -------------------------------------------------
            // SNOWFALL
            // -------------------------------------------------

            double snowfallValue =
                    0.0;


            if (snowfall != null
                    && !snowfall.get(i).isNull()) {

                snowfallValue =
                        snowfall
                                .get(i)
                                .asDouble();
            }


            // -------------------------------------------------
            // CREATE FORECAST DAY OBJECT
            // -------------------------------------------------

            ForecastDay forecastDay =
                    new ForecastDay(
                            date,
                            dayName,
                            maxTemperature,
                            minTemperature,
                            condition,
                            precipitationValue,
                            snowfallValue
                    );


            // -------------------------------------------------
            // ADD TO LIST
            // -------------------------------------------------

            forecastList.add(
                    forecastDay
            );
        }


        // =====================================================
        // STEP 10: RETURN 7-DAY FORECAST
        // =====================================================

        return forecastList;
    }


    // =========================================================
    // ASYNCHRONOUS CURRENT WEATHER
    // =========================================================

    public Future<Weather> getWeatherAsync(
            String city) {

        return executorService.submit(() -> {

            return getWeather(
                    city
            );

        });
    }


    // =========================================================
    // ASYNCHRONOUS 7-DAY FORECAST
    // =========================================================

    public Future<List<ForecastDay>> get7DayForecastAsync(
            String city) {

        return executorService.submit(() -> {

            return get7DayForecast(
                    city
            );

        });
    }


    // =========================================================
    // WEATHER CODE CONVERSION
    // =========================================================

    private String getWeatherCondition(
            int weatherCode) {

        return switch (weatherCode) {

            // -------------------------------------------------
            // CLEAR
            // -------------------------------------------------

            case 0 ->
                    "Clear Sky";


            // -------------------------------------------------
            // CLOUDY
            // -------------------------------------------------

            case 1, 2, 3 ->
                    "Mainly Clear / Cloudy";


            // -------------------------------------------------
            // FOG
            // -------------------------------------------------

            case 45, 48 ->
                    "Fog";


            // -------------------------------------------------
            // DRIZZLE
            // -------------------------------------------------

            case 51, 53, 55 ->
                    "Drizzle";


            // -------------------------------------------------
            // FREEZING DRIZZLE
            // -------------------------------------------------

            case 56, 57 ->
                    "Freezing Drizzle";


            // -------------------------------------------------
            // RAIN
            // -------------------------------------------------

            case 61, 63, 65 ->
                    "Rain";


            // -------------------------------------------------
            // FREEZING RAIN
            // -------------------------------------------------

            case 66, 67 ->
                    "Freezing Rain";


            // -------------------------------------------------
            // SNOW
            // -------------------------------------------------

            case 71, 73, 75, 77 ->
                    "Snow";


            // -------------------------------------------------
            // RAIN SHOWERS
            // -------------------------------------------------

            case 80, 81, 82 ->
                    "Rain Showers";


            // -------------------------------------------------
            // SNOW SHOWERS
            // -------------------------------------------------

            case 85, 86 ->
                    "Snow Showers";


            // -------------------------------------------------
            // THUNDERSTORM
            // -------------------------------------------------

            case 95 ->
                    "Thunderstorm";


            // -------------------------------------------------
            // THUNDERSTORM WITH HAIL
            // -------------------------------------------------

            case 96, 99 ->
                    "Thunderstorm with Hail";


            // -------------------------------------------------
            // UNKNOWN
            // -------------------------------------------------

            default ->
                    "Unknown";
        };
    }


    // =========================================================
    // DAY NAME
    // =========================================================

    private String getDayName(
            DayOfWeek dayOfWeek) {

        return switch (dayOfWeek) {

            case MONDAY ->
                    "Monday";

            case TUESDAY ->
                    "Tuesday";

            case WEDNESDAY ->
                    "Wednesday";

            case THURSDAY ->
                    "Thursday";

            case FRIDAY ->
                    "Friday";

            case SATURDAY ->
                    "Saturday";

            case SUNDAY ->
                    "Sunday";
        };
    }


    // =========================================================
    // SHUTDOWN THREAD POOL
    // =========================================================

    public void shutdown() {

        executorService.shutdown();
    }
}