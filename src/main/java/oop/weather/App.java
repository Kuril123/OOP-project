package oop.weather;

public class App {
    public static void main(String[] args) {
        try {
            WeatherProvider provider = new CollectingData();

            WeatherRepository repo = new SqliteWeatherRepository(AppConfig.DB_URL);
            repo.init();

            WeatherService service = new WeatherService(provider, repo);
            WeatherScheduler scheduler = new WeatherScheduler(service);

            System.out.println("Weather Collector started for: " + AppConfig.CITY);

            scheduler.start(
                    AppConfig.CITY,
                    AppConfig.LAT,
                    AppConfig.LON,
                    AppConfig.PERIOD_SECONDS
            );
        } catch (Exception e) {
            System.err.println("[FATAL] " + e.getMessage());
            e.printStackTrace();
        }
    }
}
