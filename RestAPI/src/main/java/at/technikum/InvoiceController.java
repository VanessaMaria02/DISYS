package at.technikum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final static String BROKE_URL = "http://localhost:8082/invoices/";
    private RabbitTamplate rabbitTamplate;


    @PostMapping("/invoices/{costumer-id}")
    public void startDataGathering(@PathVariable String costumerId) {
        if (isvalid(Id)) {
            rabbitTamplate.convertAndSend("\"data-collection-exchange\", \"start\",costumerId");
        }
    }


    @GetMapping("/invoices/{id}")
    public String getInvoice(@PathVariable("invoices/{costumer-id}") String Id) {
        if (!isvalid(id)) {
            return " 404 NOT FOUND"
        } else {




        }


    }
}

