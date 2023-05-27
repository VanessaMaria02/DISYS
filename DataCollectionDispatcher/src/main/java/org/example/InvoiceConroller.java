package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvoiceConroller {

    private final static String BROKER_URL="tcp://localhost:8082";
    @GetMapping(value = "/invoices/{customer-id}")
    //List
    @PostMapping(value = "/invoices/{costumer-id}")
    public ResponseEntity<String> generateInvoice(@PathVariable("costumer-id") String customerId) {
    //if()
        return ResponseEntity.ok("Invoice generation started for customer: " + customerId);
    }


}

