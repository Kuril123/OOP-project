package oop.weather;


public final class AppConfig {
    public static final String CITY = "Astana";
    public static final double LAT = 51.1694;
    public static final double LON = 71.4491;

    public static final String DB_URL = "jdbc:sqlite:weather.db";

    public static final long PERIOD_SECONDS = 1800; // 30 минут

    private AppConfig() {}
}
