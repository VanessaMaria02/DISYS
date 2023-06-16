package org.example;


import com.itextpdf.text.DocumentException;
import org.example.database.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdfGeneratorTest {

    @Test
    public void ShouldReturnRightString() throws DocumentException, FileNotFoundException {
        //Checks if right String after generating PDF is returned
        //Arrange
        PdfGenerator pdfGenerator = new PdfGenerator();

        PdfGenerator mochPdfGenerator = new PdfGenerator(){
            @Override
            public Customer getCutomerInfo(String customerId){
                return new Customer(0, "Vanessa", "Fichtinger");
            }
        };

        PdfGenerator pdfGenerator1 = mochPdfGenerator;


        //Act
       String message = pdfGenerator1.generatePdf("0;1.0");

       //Assert
        assertEquals( "PDF for Customer: 0 Vanessa Fichtinger generated", message);
    }

    @Test
    public void PDFshouldExist() throws DocumentException, FileNotFoundException {
        //Checks if PDF with the right Path exists
        //Arrange
        PdfGenerator pdfGenerator = new PdfGenerator();

        PdfGenerator mochPdfGenerator = new PdfGenerator(){
            @Override
            public Customer getCutomerInfo(String customerId){
                return new Customer(0, "Vanessa", "Fichtinger");
            }
        };

        PdfGenerator pdfGenerator1 = mochPdfGenerator;


        //Act
       pdfGenerator1.generatePdf("0;1.0");

        //Assert
       File pdfFile = new File("C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage\\invoice_customerID_0.pdf");
       Assertions.assertTrue(pdfFile.exists());
    }

    @Test
    public void shouldReturnErrorMessageWrongFormat() throws DocumentException, FileNotFoundException {
        //Check if right Error message is returned
        //Arrange
        PdfGenerator pdfGenerator = new PdfGenerator();

        //Act
        String message = pdfGenerator.generatePdf("Test");

        //Assert
        assertEquals("Error: wrong messaging format", message);

    }

    @Test
    public void shouldReturnDefaultCustomer(){
        //Check if getCustomerInfo() returns default customer if no customer with the right id is found
        //Arrange
        PdfGenerator pdfGenerator = new PdfGenerator();
        Customer customer = new Customer(0, "first_name", "last_name");

        //Act
        Customer customer1 = pdfGenerator.getCutomerInfo("0");

        //Assert
        assertEquals(customer.getId(), customer1.getId());
        assertEquals(customer.getFirst_name(), customer1.getFirst_name());
        assertEquals(customer.getLast_name(), customer1.getLast_name());

    }

    @Test
    public void shouldReturnRightCustomre(){
        //Check if getCustomerInfo() returns the right customer (if database changes this Test does not work anymore)
        //Arrange
        PdfGenerator pdfGenerator = new PdfGenerator();
        Customer customer = new Customer(1, "Luisa", "Colon");

        //Act
        Customer customer1 = pdfGenerator.getCutomerInfo("1");

        //Assert
        assertEquals(customer.getId(), customer1.getId());
        assertEquals(customer.getFirst_name(), customer1.getFirst_name());
        assertEquals(customer.getLast_name(), customer1.getLast_name());
    }

    @Test
    public void shouldReturnDefaultCustomer2(){
        //Check if getCustomerInfo() returns default customer if SQL Exception is thrown
        //Arrange
        PdfGenerator pdfGenerator = new PdfGenerator();
        Customer customer = new Customer(0, "first_name", "last_name");

        //Act
        Customer customer1 = pdfGenerator.getCutomerInfo("Test");

        //Assert
        assertEquals(customer.getId(), customer1.getId());
        assertEquals(customer.getFirst_name(), customer1.getFirst_name());
        assertEquals(customer.getLast_name(), customer1.getLast_name());

    }

}
