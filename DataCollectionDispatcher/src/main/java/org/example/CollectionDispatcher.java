package org.example;

import org.example.database.Database;
import org.example.database.Station;
import org.example.queue.rabbitmq;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class CollectionDispatcher {

    private static org.example.queue.rabbitmq rabbitmq = new rabbitmq();

    //starts the receiver to catch messages from RestAPI on red messaging queue
    public void startDataGatheringJob() {
        try {
            rabbitmq.recv("red");
        } catch (IOException | TimeoutException e) {
            System.out.println("Error startDataGatheringJob: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }


    //gets all stations from stationdb, and sends a Message for each to the StationDataCollector with the customerID, StationID and DatabaseURL
    //Also send a message to DataCollectionReceiver with customerID and Amount of stations
    public void getAllStations(String customerID){
        String query = "SELECT * FROM station";
        //counter for counting how many stations their are
        int counter = 0;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                // Get Data for each station
                int id = rs.getInt("id");
                String db_url = rs.getString("db_url");

                //creat message
                String message = customerID + ";" + id + ";" + db_url;
                //send message to StationDataCollector on the green queue
                rabbitmq.send("green", message);
                counter++;
            }

        } catch (SQLException e) {
            System.out.println("Error getAllStations: "+e.getMessage());
        }
        //send amount of stations to the DataCollectionReceiver
        rabbitmq.send("purple", customerID+";"+counter);
    }

}
