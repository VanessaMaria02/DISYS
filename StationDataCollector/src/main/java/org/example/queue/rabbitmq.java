package org.example.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.StationCollector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;


public class rabbitmq {
    private final String host = "localhost"; // RabbitMQ host
    private final int port = 30003;     // RabbitMQ port


    private static StationCollector stationCollector = new StationCollector();

    //Information and Code about rabbitMq From: https://www.rabbitmq.com/tutorials/tutorial-one-java.html

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

            System.out.println(" [StationDataCollector] Sent '" + message + "'");

        } catch (IOException | TimeoutException e) {
            System.out.println("Error send StationDataCollector: "+e.getMessage());
        }
    }

    public void recv (String queue_Name) throws IOException, TimeoutException {
        ConnectionFactory factory = getConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queue_Name, false, false, false, null);
        System.out.println(" [StationDataCollector] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [StationDataCollector] Received '" + message + "'");

            //starts the Data gathering from the station with the customerID
            stationCollector.getCustomerData(message);

        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
    }
}


