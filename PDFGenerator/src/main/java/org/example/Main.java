package org.example;

import org.example.queue.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    private static PdfGenerator pdfGenerator = new PdfGenerator();
    public static void main(String[] args)  {
        pdfGenerator.startWork();

    }
}