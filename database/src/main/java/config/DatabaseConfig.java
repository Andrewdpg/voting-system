package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static HikariDataSource dataSource;

    public static void initialize(String databaseHost) {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalStateException("database.properties file not found");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load database.properties", ex);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + databaseHost + "/votaciones");
        config.setUsername(properties.getProperty("username"));
        config.setPassword(properties.getProperty("password"));

        config.setMaximumPoolSize(35);
        config.setMinimumIdle(20);
        config.setConnectionTimeout(15000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1800000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("tcpKeepAlive", "true");
        config.addDataSourceProperty("applicationName", "MyApp");
        config.addDataSourceProperty("socketTimeout", "30");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}