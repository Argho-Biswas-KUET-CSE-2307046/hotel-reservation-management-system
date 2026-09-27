package hotel.model;


// =========================================================
// FORECAST DAY MODEL
// =========================================================

public class ForecastDay {

    // =========================================================
    // FIELDS
    // =========================================================

    private String date;

    private String dayName;

    private double maxTemperature;

    private double minTemperature;

    private String condition;

    private double precipitation;

    private double snowfall;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public ForecastDay() {
    }


    // =========================================================
    // PARAMETERIZED CONSTRUCTOR
    // =========================================================

    public ForecastDay(
            String date,
            String dayName,
            double maxTemperature,
            double minTemperature,
            String condition,
            double precipitation,
            double snowfall) {

        this.date = date;

        this.dayName = dayName;

        this.maxTemperature =
                maxTemperature;

        this.minTemperature =
                minTemperature;

        this.condition =
                condition;

        this.precipitation =
                precipitation;

        this.snowfall =
                snowfall;
    }


    // =========================================================
    // GET DATE
    // =========================================================

    public String getDate() {

        return date;
    }


    // =========================================================
    // SET DATE
    // =========================================================

    public void setDate(
            String date) {

        this.date = date;
    }


    // =========================================================
    // GET DAY NAME
    // =========================================================

    public String getDayName() {

        return dayName;
    }


    // =========================================================
    // SET DAY NAME
    // =========================================================

    public void setDayName(
            String dayName) {

        this.dayName = dayName;
    }


    // =========================================================
    // GET MAXIMUM TEMPERATURE
    // =========================================================

    public double getMaxTemperature() {

        return maxTemperature;
    }


    // =========================================================
    // SET MAXIMUM TEMPERATURE
    // =========================================================

    public void setMaxTemperature(
            double maxTemperature) {

        this.maxTemperature =
                maxTemperature;
    }


    // =========================================================
    // GET MINIMUM TEMPERATURE
    // =========================================================

    public double getMinTemperature() {

        return minTemperature;
    }


    // =========================================================
    // SET MINIMUM TEMPERATURE
    // =========================================================

    public void setMinTemperature(
            double minTemperature) {

        this.minTemperature =
                minTemperature;
    }


    // =========================================================
    // GET CONDITION
    // =========================================================

    public String getCondition() {

        return condition;
    }


    // =========================================================
    // SET CONDITION
    // =========================================================

    public void setCondition(
            String condition) {

        this.condition =
                condition;
    }


    // =========================================================
    // GET PRECIPITATION
    // =========================================================

    public double getPrecipitation() {

        return precipitation;
    }


    // =========================================================
    // SET PRECIPITATION
    // =========================================================

    public void setPrecipitation(
            double precipitation) {

        this.precipitation =
                precipitation;
    }


    // =========================================================
    // GET SNOWFALL
    // =========================================================

    public double getSnowfall() {

        return snowfall;
    }


    // =========================================================
    // SET SNOWFALL
    // =========================================================

    public void setSnowfall(
            double snowfall) {

        this.snowfall =
                snowfall;
    }


    // =========================================================
    // TO STRING
    // =========================================================

    @Override
    public String toString() {

        return "ForecastDay{" +

                "date='" + date + '\'' +

                ", dayName='" + dayName + '\'' +

                ", maxTemperature=" +
                maxTemperature +

                ", minTemperature=" +
                minTemperature +

                ", condition='" +
                condition + '\'' +

                ", precipitation=" +
                precipitation +

                ", snowfall=" +
                snowfall +

                '}';
    }
}