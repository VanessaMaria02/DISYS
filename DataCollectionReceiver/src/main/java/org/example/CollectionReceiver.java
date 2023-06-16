package org.example;

import org.example.queue.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CollectionReceiver {

    private static rabbitmq rabbitmq = new rabbitmq();
    public void startReceiver(){
        try {
            rabbitmq.recv("purple");
        } catch (IOException | TimeoutException e) {
            System.out.println("Error startReceiver: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void startSecondReceiver(String message){
        try {
            rabbitmq.Countrecv("blue", message);
        } catch (IOException | TimeoutException e) {
            System.out.println("Error startSecondReceiver: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
