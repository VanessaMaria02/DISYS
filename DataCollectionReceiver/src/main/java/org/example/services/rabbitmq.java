package org.example.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class rabbitmq {
    private final String host = "localhost"; // RabbitMQ host
    private final int port = 30003; // RabbitMQ port

    public void send (String queue_Name, String message){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue_Name, false, false, false, null);
            channel.basicPublish("", queue_Name, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    public String recv (String queue_Name) throws IOException, TimeoutException {
        AtomicReference<String> message = new AtomicReference<>("");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queue_Name, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            message.set(new String(delivery.getBody(), StandardCharsets.UTF_8));
            System.out.println(" [x] Received '" + message + "'");
            // the data collection reveiver gets the information how many stations their are
            //then he waits until he gets that many messages and add alle the ergebnisse zusammen
            //and sends them with the customer id to the pdf generator
            try {
                Countrecv("blue", String.valueOf(message));
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
        return message.get();
    }

    public String Countrecv (String queue_Name, String howmany) throws IOException, TimeoutException {

        String amount = "";
        String customeridT = "";

        String[] parts = howmany.split(";");


        if(parts.length == 2){
            customeridT = parts[0];
            amount = parts[1];

            System.out.println("customerID: "+customeridT);
            System.out.println("amount: "+amount);
        }else {
            customeridT = "";
            System.out.println("Ungültiges Format der Message.");
        }

        final String customerid = customeridT;

        int hmany = Integer.parseInt(amount);
        AtomicInteger counter = new AtomicInteger();
        AtomicReference<String> message = new AtomicReference<>("");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queue_Name, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        System.out.println(queue_Name);

        AtomicReference<Double> finalkwh = new AtomicReference<>((double) 0);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            message.set(new String(delivery.getBody(), StandardCharsets.UTF_8));
            System.out.println("test");
            System.out.println(" [x] Received '" + message + "'");


            String message2 = String.valueOf(message);

            String[] parts2 = message2.split(";");





            String customerId2 = "";
            String kwh = "";


            if(parts2.length == 2){
                customerId2 = parts2[0];
                kwh = parts2[1];

                System.out.println("customerID: "+customerId2);
                System.out.println("kwh: "+kwh);
            }else {
                System.out.println("Ungültiges Format der Message.");
            }


            if(customerId2.equals(customerid)){
                counter.getAndIncrement();
                System.out.println(counter);

                double kwhDouble = Double.parseDouble(kwh);

                finalkwh.set(finalkwh.get() + kwhDouble);
                System.out.println(finalkwh.get());

            }


            if(Integer.parseInt(String.valueOf(counter)) == hmany){
                String finalmessage = customerid+";"+finalkwh;
                send("yellow", finalmessage);
                counter.set(0);
                finalkwh.set(0.0);
                try {
                    channel.close();
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
        return message.get();
    }
}




