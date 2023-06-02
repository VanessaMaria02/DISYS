package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.services.rabbitmq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Main {

    private static rabbitmq rabbitmq = new rabbitmq();

    public static void main(String[] args) throws IOException, TimeoutException {

       rabbitmq.recv("red");


    }
}