package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @PostMapping("/{customer-id}")
    public ResponseEntity<String> startDataGathering(@PathVariable("customer-id") String customerId) {
        // Start the data gathering job and send a start message with the customer ID
        // to the Data Collection Dispatcher
        // Implement the logic here

        return ResponseEntity.ok("Data gathering job started");
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<String> getInvoice(@PathVariable("customer-id") String customerId) {
        // Retrieve the invoice PDF with download link and creation time
        // Implement the logic here


        return ResponseEntity.notFound().build(); // Return 404 Not Found if the invoice is not available
    }
}

