package org.example;

import org.example.services.rabbitmq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class StationCollector {
    private static org.example.services.rabbitmq rabbitmq = new rabbitmq();

    public void getCustomerData(String message){

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
            System.out.println("Ungültiges Format der Message.");
        }

        String query = "SELECT * FROM charge WHERE customer_id = "+customerID;

        double allKwh = 0;

        try (
                Connection conn = Database.getConnection(db_url);
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                int id = rs.getInt("id");
                double kwh = rs.getDouble("kwh");
                int customer = rs.getInt("customer_id");

                Charge charge = new Charge(id, kwh, customer);
                System.out.println(charge);

                allKwh += kwh;



            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        String finalmessage = customerID + ";" + allKwh;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        rabbitmq.send("blue", finalmessage);

    }
}
