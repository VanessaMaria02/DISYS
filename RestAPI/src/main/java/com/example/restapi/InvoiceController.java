package com.example.restapi;


import com.example.restapi.services.rabbitmq;
import org.springframework.web.bind.annotation.*;

@RestController
public class InvoiceController {
    private rabbitmq rabbitmq = new rabbitmq();


    @PostMapping("/invoices/{id}")
    public void startDataGathering(@PathVariable String id) {
        rabbitmq.send("red", id);
    }


    @GetMapping("/invoices/{id}")
    public String getInvoice(@PathVariable String id) {
        return "not implementet jet";
    }
}
