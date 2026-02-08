package oop.weather;


public interface WeatherRepository {
    void init() throws Exception;
    void save(WeatherData data) throws Exception;
}
