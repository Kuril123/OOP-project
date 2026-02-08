package oop.weather;

public class WeatherService {

    private final WeatherProvider provider;
    private final WeatherRepository repository;

    public WeatherService(WeatherProvider provider, WeatherRepository repository) {
        this.provider = provider;
        this.repository = repository;
    }

    public WeatherData collectAndStore(String city, double lat, double lon) throws Exception {
        WeatherData data = provider.fetchCurrentWeather(city, lat, lon);
        repository.save(data);
        return data;
    }
}
