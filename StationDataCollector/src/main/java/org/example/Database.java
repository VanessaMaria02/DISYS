package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final static String DRIVER = "postgresql";
    private final static String DATABASE_NAME = "stationdb";
    private final static String USERNAME = "postgres";
    private final static String PASSWORD = "postgres";

    public static Connection getConnection(String db_URL) throws SQLException {
        return DriverManager.getConnection(getUrl(db_URL));
    }

    private static String getUrl(String dB_URL) {
        // jdbc:DRIVER://HOST:PORT/DATABASE_NAME
        // ?user=USERNAME&password=PASSWORD
        return String.format(
                "jdbc:%s://%s/%s?user=%s&password=%s",
                DRIVER,
                dB_URL,
                DATABASE_NAME,
                USERNAME,
                PASSWORD
        );
    }
}
