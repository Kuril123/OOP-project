package oop.weather;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * WeatherScheduler — запускает сбор погоды по расписанию.
 */
public class WeatherScheduler {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final WeatherService service;

    public WeatherScheduler(WeatherService service) {
        this.service = service;
    }

    public void start(String city, double lat, double lon, long periodSeconds) {
        Runnable job = () -> {
            try {
                WeatherData data = service.collectAndStore(city, lat, lon);
                System.out.println("[OK] Saved: " + data);
            } catch (Exception e) {
                System.err.println("[ERROR] " + e.getMessage());
                e.printStackTrace();
            }
        };

        executor.scheduleAtFixedRate(job, 0, periodSeconds, TimeUnit.SECONDS);
    }
}
