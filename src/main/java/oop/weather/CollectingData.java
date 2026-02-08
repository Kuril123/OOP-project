package oop.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;


public class CollectingData implements WeatherProvider {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public WeatherData fetchCurrentWeather(String city, double lat, double lon) throws Exception {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + lat
                + "&longitude=" + lon
                + "&current=temperature_2m,wind_speed_10m,weather_code"
                + "&timezone=auto";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Open-Meteo error: HTTP " + response.statusCode() + " body=" + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        JsonNode current = root.get("current");
        if (current == null || current.isNull()) {
            throw new RuntimeException("No 'current' field in response");
        }

        double temp = current.get("temperature_2m").asDouble();
        double wind = current.get("wind_speed_10m").asDouble();
        int code = current.get("weather_code").asInt();

        String timeStr = current.get("time").asText();
        Instant sourceTime;
        try {
            sourceTime = Instant.parse(timeStr.length() == 16 ? (timeStr + ":00Z") : timeStr);
        } catch (Exception e) {
            sourceTime = Instant.now();
        }

        return new WeatherData(city, temp, wind, code, sourceTime);
    }
}
