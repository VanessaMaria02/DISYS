package org.example;

import org.example.services.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    private static rabbitmq rabbitmq = new rabbitmq();
    public static void main(String[] args) throws IOException, TimeoutException {
        rabbitmq.recv("purple");
    }
}