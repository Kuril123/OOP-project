package oop.weather;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class SqliteWeatherRepository implements WeatherRepository {

    private final String dbUrl;

    public SqliteWeatherRepository(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public void init() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS weather_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "city TEXT NOT NULL," +
                "temperature_c REAL," +
                "wind_speed_ms REAL," +
                "weather_code INTEGER," +
                "source_time TEXT," +
                "created_at TEXT DEFAULT (datetime('now'))" +
                ");";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    @Override
    public void save(WeatherData data) throws Exception {
        String sql = "INSERT INTO weather_records (city, temperature_c, wind_speed_ms, weather_code, source_time) " +
                "VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, data.getCity());
            ps.setDouble(2, data.getTemperatureC());
            ps.setDouble(3, data.getWindSpeedMs());
            ps.setInt(4, data.getWeatherCode());
            ps.setString(5, data.getSourceTime().toString());

            ps.executeUpdate();
        }
    }
}
