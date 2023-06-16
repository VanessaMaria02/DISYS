package org.example;

import org.example.queue.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    private static CollectionReceiver collectionReceiver = new CollectionReceiver();
    public static void main(String[] args) {
     collectionReceiver.startReceiver();
    }
}