package oop.weather;

import java.time.Instant;

public class WeatherData {
    private final String city;
    private final double temperatureC;
    private final double windSpeedMs;
    private final int weatherCode;
    private final Instant sourceTime;

    public WeatherData(String city, double temperatureC, double windSpeedMs, int weatherCode, Instant sourceTime) {
        this.city = city;
        this.temperatureC = temperatureC;
        this.windSpeedMs = windSpeedMs;
        this.weatherCode = weatherCode;
        this.sourceTime = sourceTime;
    }

    public String getCity() { return city; }
    public double getTemperatureC() { return temperatureC; }
    public double getWindSpeedMs() { return windSpeedMs; }
    public int getWeatherCode() { return weatherCode; }
    public Instant getSourceTime() { return sourceTime; }

    @Override
    public String toString() {
        return "WeatherData{" +
                "city='" + city + '\'' +
                ", temperatureC=" + temperatureC +
                ", windSpeedMs=" + windSpeedMs +
                ", weatherCode=" + weatherCode +
                ", sourceTime=" + sourceTime +
                '}';
    }
}
