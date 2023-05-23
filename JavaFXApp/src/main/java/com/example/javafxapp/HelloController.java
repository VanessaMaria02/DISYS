package com.example.javafxapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HelloController {
    @FXML
    private TextField userId;

    @FXML
    private Label pdfStatues;
    private Timeline timeline;

    @FXML
    protected void onGetInvoice()  {
        pdfStatues.setText("sending request");

        String cuID = userId.getText();
        String urlString = "http://localhost:8082/invoices/" + cuID;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();


                    HttpResponse<String> response = HttpClient.newHttpClient()
                            .send(request, HttpResponse.BodyHandlers.ofString());

                    timeline = new Timeline(new KeyFrame(Duration.seconds(5), (ActionEvent event) ->{
                        checkInvoiceStatus(urlString);
                    }));
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play();


                    if(response.statusCode() == 200){
                        pdfStatues.setText("waiting for Pdf");
                   }else{
                        pdfStatues.setText("Error: Failed to retrieve PDF");
                   }

        }catch (URISyntaxException | IOException | InterruptedException e){
            pdfStatues.setText("Error: "+ e.getMessage());
        }

        userId.setText("Customer Id");
    }

    private void checkInvoiceStatus(String url){

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            pdfStatues.setText("Got PDF");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Error in invoiceStatusCheck: "+e.getMessage());
        }
    }
}