package org.example.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
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
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //the station data collector should make a datenbank abfrage with the right port+
            //then it should send the ergebniss to the data collection receiver
            send("blue", String.valueOf(message));

        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
        return message.get();
    }
}


