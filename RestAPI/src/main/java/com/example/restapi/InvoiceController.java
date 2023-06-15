package com.example.restapi;


import com.example.restapi.services.rabbitmq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
public class InvoiceController {
    private rabbitmq rabbitmq = new rabbitmq();


    @PostMapping("/invoices/{id}")
    public void startDataGathering(@PathVariable String id) {
        deleteFile(id);
        rabbitmq.send("red", id);
    }


    @GetMapping("/invoices/{id}")
    public ResponseEntity<String> getInvoice(@PathVariable int id) {
        //information about http statues from: https://stackabuse.com/how-to-return-http-status-codes-in-a-spring-boot-application/
        if(isFileExisting(id)){
            return ResponseEntity.ok("http://localhost:8082/download/"+id);

        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }

    }

    // Information about checking if a File exists from: https://www.techiedelight.com/de/check-if-file-exists-java/
    public static boolean isFileExisting(int customerID){
        String fileUrl = "C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage\\invoice_customerID_"+customerID+".pdf";
        File file = new File(fileUrl);

        return file.exists() && !file.isDirectory();
    }

    public void deleteFile(String customerID){
        String fileUrl = "C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage\\invoice_customerID_"+customerID+".pdf";
        File file = new File(fileUrl);

        if(file.delete()){
            System.out.println(fileUrl+" deleted");
        }else {
            System.out.println("nothing deleted");
        }
    }


}
