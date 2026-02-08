package oop.weather;


public interface WeatherProvider {
    WeatherData fetchCurrentWeather(String city, double lat, double lon) throws Exception;
}
