package com.example.restapi;


import com.example.restapi.queue.rabbitmq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
public class InvoiceController {
    private rabbitmq rabbitmq = new rabbitmq();


   //path to start DataGathering Job
    @PostMapping("/invoices/{id}")
    public void startDataGathering(@PathVariable String id) {
        //deleteFile old file with same Customerid
        deleteFile(id);
        //Send message with customerid to the DataCollectionDispatcher on the red messaging queue
        rabbitmq.send("red", id);
    }


   //Path to check if PDF exists and to get downloadlink
    @GetMapping("/invoices/{id}")
    public ResponseEntity<String> getInvoice(@PathVariable int id) {
        //information about http statues from: https://stackabuse.com/how-to-return-http-status-codes-in-a-spring-boot-application/
        //check if pdf exists
        if(isFileExisting(id)){
            //sends statuesCode 200 and downloadlink for pdf
            return ResponseEntity.ok("http://localhost:8082/download/"+id);
        }else {
            //send Statues Code 404 NOT FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }

    }

    // Information about checking if a File exists from: https://www.techiedelight.com/de/check-if-file-exists-java/
    public static boolean isFileExisting(int customerID){
        //set url where file should exist
        String fileUrl = "C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage\\invoice_customerID_"+customerID+".pdf";
        //create File Object with url
        File file = new File(fileUrl);
        //check if file exists and if it is no directory
        return file.exists() && !file.isDirectory();
    }

    //deleted old file with customer id if it exists
    public void deleteFile(String customerID){
        //set url where File could be
        String fileUrl = "C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage\\invoice_customerID_"+customerID+".pdf";
        //creat new File Object with url
        File file = new File(fileUrl);

        //delete File
        if(file.delete()){
            //if File is deleted
            System.out.println(fileUrl+" deleted");
        }else {
            //if no file is deleted (No file existed or a problem happened)
            System.out.println("nothing deleted");
        }
    }


}
