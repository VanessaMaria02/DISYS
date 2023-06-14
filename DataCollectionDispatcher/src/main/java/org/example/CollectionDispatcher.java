package org.example;

import org.example.services.rabbitmq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CollectionDispatcher {

    private static org.example.services.rabbitmq rabbitmq = new rabbitmq();

    public void getAllStations(String customerID){
        String query = "SELECT * FROM station";
        int counter = 0;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String db_url = rs.getString("db_url");
                double lat = rs.getDouble("lat");
                double lng = rs.getDouble("lng");

                Station station = new Station(id, db_url, lat, lng);
                System.out.println(station);

                String message = customerID + ";" + id + ";" + db_url;
                rabbitmq.send("green", message);
                counter++;

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("counter:"+counter);
        rabbitmq.send("purple", customerID+";"+counter);
    }

}
