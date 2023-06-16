package org.example;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    private static CollectionDispatcher collectionDispatcher = new CollectionDispatcher();

    public static void main(String[] args) {

        collectionDispatcher.startDataGatheringJob();

    }
}