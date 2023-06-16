package com.example.javafxapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    //downloadLink is a TextField instead of a Label to make it easier to copy the download link
    @FXML
    private TextField downloadlink;

    @FXML
    private Button button;

    private Timeline timeline;

    //function that starts when the button is clicked
    @FXML
    protected void onGetInvoice()  {
        //disable button so only one request at a time can be made
        button.setDisable(true);
        //reset downloadlink TextField
        downloadlink.setText("waiting for downloadlink");
        //set
        pdfStatues.setText("sending request");

        //get CustomerID from the userId Textfield
        String cuID = userId.getText();
        //generate Url for starting the dataGatheringJob
        String urlString = "http://localhost:8082/invoices/" + cuID;

        //start a POST HttpRequest
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();


                    HttpResponse<String> response = HttpClient.newHttpClient()
                            .send(request, HttpResponse.BodyHandlers.ofString());

                    //Check if the POST HttpRequest was successful
                    if(response.statusCode() == 200){
                        //set the pdfStatues Label
                        pdfStatues.setText("waiting for Pdf");
                   }else{
                        pdfStatues.setText("Error: Failed to retrieve PDF");
                        //enable the button so a new request can be started
                        button.setDisable(false);
                   }

                    //information about java timeline from: https://www.educba.com/javafx-timeline/

            //check every five seconds if a PDF for the customerID is available
            timeline = new Timeline(new KeyFrame(Duration.seconds(5), (ActionEvent event) ->{
                checkInvoiceStatus(urlString, cuID);
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

        }catch (URISyntaxException | IOException | InterruptedException e){
            pdfStatues.setText("Error POST HTTP-Request: "+ e.getMessage());
            //enable the button so a new request can be started
            button.setDisable(false);
        }

        //reset the TextField
        userId.setText("Customer Id");
    }

    private void checkInvoiceStatus(String url, String customerID){

        //start a GET Http Request
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            //Check the response of the HttpRequest
            if(response.statusCode() == 404){
                //404 means there is no PDF jet in the FileStorage
                //set the pdfStatues Label
                pdfStatues.setText("CustomerID "+customerID+": Still waiting for Pdf");
                //set the downloadlink Textfield
                downloadlink.setText("waiting for downloadlink");
                //Print response for debugging
                System.out.println(response);
            }else if(response.statusCode() == 200){
                //200 means a PDF for the CustomerID exists
                //set the pdfStatues Label
                pdfStatues.setText("CustomerID "+customerID+": Got PDF");
                //set the Text field with the downloadlink for the pdf(Creationtime of the pdf can be found in the pdf)
                downloadlink.setText(response.body());
                //Print response for debugging
                System.out.println(response);
                //enable the button so a new request can be made
                button.setDisable(false);
                //end the timeline so no new GET HTTP-REQUEST for this Request will be made
                timeline.stop();
            }else {
                //set pdfStaties to let the user know their was a problem
                pdfStatues.setText("Error: Failed to retrieve PDF");
                System.out.println("Error checking pdf Statues");
                //enable button so a new request can be made
                button.setDisable(false);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Error in invoiceStatusCheck: "+e.getMessage());
            button.setDisable(false);
        }
    }





}