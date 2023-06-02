package org.example.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.sun.jdi.IntegerValue;

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
        int hmany = Integer.parseInt(howmany);
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
            String finalmessage = "";
            message.set(new String(delivery.getBody(), StandardCharsets.UTF_8));
            finalmessage += message;
            System.out.println(" [x] Received '" + message + "'");
            counter.getAndIncrement();
            if(Integer.parseInt(String.valueOf(counter)) == hmany){
                send("yellow", finalmessage);
            }
        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
        return message.get();
    }
}


