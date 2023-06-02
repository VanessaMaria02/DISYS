package org.example;

import org.example.services.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    private static rabbitmq rabbitmq = new rabbitmq();
    public static void main(String[] args) throws IOException, TimeoutException {

        //the pdf generator waits for gets an message and creats an pdf with it
        //it then stores the pdf in the ordner filestorage
        rabbitmq.recv("yellow");
    }
}