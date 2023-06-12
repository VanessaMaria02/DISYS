package org.example.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.CollectionDispatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class rabbitmq {

    private static CollectionDispatcher collectionDispatcher = new CollectionDispatcher();
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

    public void recv (String queue_Name) throws IOException, TimeoutException {
        AtomicInteger counter = new AtomicInteger();
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
            // the data Collection Dispatcher should get from the stations db how many station their are and their port
            //then it should send the data collection receiver how many messages he should accept
            //and it should send to the station data collector the ids of the database, the port, and the customer ids
            //for each station their should be a own message
            System.out.println(" [x] Received '" + message + "'");
            collectionDispatcher.getAllStations(String.valueOf(message));

        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });


    }
}


