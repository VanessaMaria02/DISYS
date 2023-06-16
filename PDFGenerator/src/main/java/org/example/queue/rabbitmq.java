package org.example.queue;

import com.itextpdf.text.DocumentException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.PdfGenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

//Information and Code about rabbitMq From: https://www.rabbitmq.com/tutorials/tutorial-one-java.html

public class rabbitmq {
    private final String host = "localhost"; // RabbitMQ host
    private final int port = 30003; // RabbitMQ port

    private static PdfGenerator pdfGenerator = new PdfGenerator();

    public ConnectionFactory getConnectionFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        return factory;
    }


    public void recv (String queue_Name) throws IOException, TimeoutException {
        ConnectionFactory factory = getConnectionFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queue_Name, false, false, false, null);
        System.out.println(" [PDFGenerator] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [PDFGenerator] Received '" + message + "'");

            try {
                //starts the process of generating a invoice PDF
                pdfGenerator.generatePdf(message);
            } catch (DocumentException e){
                System.out.println("Error PDFGenerator recv: "+e.getMessage());
                throw new RuntimeException(e);
            }

            System.out.println("End!");
        };
        channel.basicConsume(queue_Name, true, deliverCallback, consumerTag -> { });
    }
}


