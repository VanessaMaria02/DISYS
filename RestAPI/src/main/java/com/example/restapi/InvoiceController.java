package com.example.restapi;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final static String BROKE_URL = "http://localhost:8082/invoices/";
    private RabbitTemplate rabbitTamplate;


    @PostMapping("/invoices/{costumer-id}")
    public void startDataGathering(@PathVariable String costumerId) {
        if (isvalid(costumerId)) {
            rabbitTamplate.convertAndSend("\"data-collection-exchange\", \"start\",costumerId");
        }
    }


    @GetMapping("/invoices/{id}")
    public String getInvoice(@PathVariable("invoices/{costumer-id}") String Id) {
        if (!isvalid(Id)) {
            return " 404 NOT FOUND";
        } else {




        }


    }
}
