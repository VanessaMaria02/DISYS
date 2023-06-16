package org.example;

import org.example.database.Charge;
import org.example.database.Database;
import org.example.queue.rabbitmq;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class StationCollector {
    private static org.example.queue.rabbitmq rabbitmq = new rabbitmq();

    //starts the consumer to receive messages from the DataCollectionDispatcher
    public void startWork(){
        try {
            rabbitmq.recv("green");
        } catch (IOException | TimeoutException e) {
            System.out.println("Error startWorker: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public String getCustomerData(String message){

        //splits message into customerId, db_url, and stationID
        String[] parts = message.split(";");

        String customerID = "";
        String db_url = "";
        String id2 = "";

        if(parts.length == 3){
            customerID = parts[0];
            id2 = parts[1];
            db_url = parts[2];
            System.out.println("customerID: " + customerID);
            System.out.println("id: " + id2);
            System.out.println("db_url: " + db_url);
        }else {
            System.out.println("Error: Wrong messaging format");
            rabbitmq.send("blue", "0;0");
            return "Error: Wrong messaging format";
        }


        //get Data from Database
       List<Charge> charges = getChargeList(customerID, db_url);


        //add upp kwH
        double allKwh = 0;
        if(charges != null){
            for (Charge charge: charges) {
                allKwh += charge.getKwh();
            }
        }



        //creat message with customerID and the whole kwh amount
        String finalmessage = customerID + ";" + allKwh;

        //thread.sleep to creat a waiting time so the pdf check shows how it looks like when no pdf exists
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Error sleep getCustomerData: "+e.getMessage());
            throw new RuntimeException(e);
        }

        //send the message to the PDFGenerator
        rabbitmq.send("blue", finalmessage);

        return finalmessage;

    }

    public List<Charge> getChargeList(String customerId, String db_url){
        //SQL Statement for getting alle charges with the same customerID

        String query = "SELECT * FROM charge WHERE customer_id = "+customerId;

        List<Charge> charges = new LinkedList<>();

        try (
                Connection conn = Database.getConnection(db_url);
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                //get charges from station
                int id = rs.getInt("id");
                double kwh = rs.getDouble("kwh");
                int customer_id = rs.getInt("customer_id");

                Charge charge = new Charge(id, kwh, customer_id);

                //save charges in List
                charges.add(charge);

            }

        } catch (SQLException e) {
            System.out.println("Error sql getCustomerData: "+e.getMessage());
        }
        return charges;
    }
}
