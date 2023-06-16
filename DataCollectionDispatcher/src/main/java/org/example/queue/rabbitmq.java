package org.example.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.CollectionDispatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


public class rabbitmq {

    //Information and Code about rabbitMq From: https://www.rabbitmq.com/tutorials/tutorial-one-java.html

    private static CollectionDispatcher collectionDispatcher = new CollectionDispatcher();
    private final String host = "localhost"; // RabbitMQ host
    private final int port = 30003; // RabbitMQ port

    public void send (String queue_Name, String message){

        ConnectionFactory factory = createConnectionFactory();

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue_Name, false, false, false, null);
            channel.basicPublish("", queue_Name, null, message.getBytes(StandardCharsets.UTF_8));

            System.out.println(" [CollectionDispatcher] Sent '" + message + "'");

        } catch (IOException | TimeoutException e) {
            System.out.println("Error send CollectionDispatcher : "+e.getMessage());
        }
    }



    public void recv (String queue_Name) throws IOException, TimeoutException {
        ConnectionFactory factory = createConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queue_Name, false, false, false, null);
        System.out.println(" [CollectionDispatcher] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
           String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [CollectionDispatcher] Received '" + message + "'");
            //starts the actual dataGathering process
            collectionDispatcher.getAllStations(message);

        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
    }

    private ConnectionFactory createConnectionFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        return factory;
    }
}


