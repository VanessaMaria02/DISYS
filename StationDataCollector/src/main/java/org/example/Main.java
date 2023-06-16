package org.example;

import org.example.queue.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    private static StationCollector stationCollector = new StationCollector();

    public static void main(String[] args)  {
        stationCollector.startWork();
    }
}