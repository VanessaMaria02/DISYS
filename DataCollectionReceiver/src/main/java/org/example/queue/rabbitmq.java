package org.example.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.CollectionReceiver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//Information and Code about rabbitMq From: https://www.rabbitmq.com/tutorials/tutorial-one-java.html
public class rabbitmq {
    private final String host = "localhost"; // RabbitMQ host
    private final int port = 30003; // RabbitMQ port

    private static CollectionReceiver collectionReceiver = new CollectionReceiver();

    public ConnectionFactory getConnectionFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        return factory;
    }

    public void send (String queue_Name, String message){

        ConnectionFactory factory = getConnectionFactory();

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queue_Name, false, false, false, null);
            channel.basicPublish("", queue_Name, null, message.getBytes(StandardCharsets.UTF_8));

            System.out.println(" [CollectionReceiver] Sent '" + message + "'");

        } catch (IOException | TimeoutException e) {
            System.out.println("Error send CollectionReceiver: "+e.getMessage());
        }
    }


    public void recv (String queue_Name) throws IOException, TimeoutException {

        ConnectionFactory factory = getConnectionFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queue_Name, false, false, false, null);
        System.out.println(" [CollectionReceiver] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [CollectionReceiver] Received '" + message + "'");
            //starts the receiver that waits for the messages with the customerId and amount of kwh per station
            collectionReceiver.startSecondReceiver(message);
        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
    }

    public void Countrecv (String queue_Name, String howmany) throws IOException, TimeoutException {

        //Spliting the messaging string into the variables amount (amount of stations that are available) and customerID
        String amount = "";
        String customeridT = "";

        String[] parts = howmany.split(";");


        if(parts.length == 2){
            customeridT = parts[0];
            amount = parts[1];
        }else {
            customeridT = "";
            System.out.println("Error: Wrong messaging format."+howmany);
        }

        //set customerid on a final String so it can be used in a lambada function
        final String customerid = customeridT;

        int hmany = Integer.parseInt(amount);

        //Use atomic variables so they can be used in a lambada function
        //counter for checking if enough messages are received
        AtomicInteger counter = new AtomicInteger();
        //final kwh to add-up the kwh from the different stations
        AtomicReference<Double> finalkwh = new AtomicReference<>((double) 0);

        ConnectionFactory factory = getConnectionFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queue_Name, false, false, false, null);
        System.out.println(" [CollectionReceiver] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [CollectionReceiver] Received '" + message + "'");

            //split message into customerid and kwh
            String[] parts2 = message.split(";");

            String customerId2 = "";
            String kwh = "";


            if(parts2.length == 2){
                customerId2 = parts2[0];
                kwh = parts2[1];

                System.out.println("customerID: "+customerId2);
                System.out.println("kwh: "+kwh);
            }else {
                System.out.println("Error: Wrong messaging format."+message);
            }


            //check if the received message is for the right Customer
            if(customerId2.equals(customerid)){
                //increment counter
                counter.getAndIncrement();

                double kwhDouble = Double.parseDouble(kwh);

                //add alle kwh so that the total amount can be send to the pdfGenerator
                finalkwh.set(finalkwh.get() + kwhDouble);
            }else{
                System.out.println("Error: Something went wrong. Do not send to much requests at once. Restart the Program!");
            }


            //Check if enough message to send the total amount of kwh are received
            if(Integer.parseInt(String.valueOf(counter)) == hmany){
                //creat String to send to pdfGenerator with customerId and Total amount of kwh
                String finalmessage = customerid+";"+finalkwh;
                //send message to the pdfGenerator on the yellow queue
                send("yellow", finalmessage);
                //reset the counter for the next request
                counter.set(0);
                //reset the kwh for the next request
                finalkwh.set(0.0);
                try {
                    channel.close();
                } catch (TimeoutException e) {
                    System.out.println("Error Countrev: "+e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
    }
}




