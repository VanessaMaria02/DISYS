package org.example;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.database.Customer;
import org.example.database.Database;
import org.example.queue.rabbitmq;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeoutException;

public class PdfGenerator {

    private static org.example.queue.rabbitmq rabbitmq = new rabbitmq();

    //generate pdf
    public void generatePdf(String message) throws DocumentException, FileNotFoundException {

        //split the message into customerId and kwh
        String[] parts = message.split(";");

        String customerID = "";
        String kwh = "";

        if(parts.length == 2){
            customerID = parts[0];
            kwh = parts[1];
        }else {
            System.out.println("Error: wrong messaging format");
        }

        Customer customer = getCutomerInfo(customerID);

        //if their was no customer with the id a default message is written in the pdf
        if(customer.getFirst_name().equals("first_name")){
            customer.setId(Integer.parseInt(customerID));
            customer.setFirst_name("No Customer");
            customer.setLast_name("With this ID");
        }

        //creat timestamp to save the creation time in the pdf
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //set Strings for invoice PDF
        String customerInfo = "Customer: "+customer.getId() + " " + customer.getFirst_name()+ " " + customer.getLast_name();
        String price = "Price: 33,1364 ct/kWh";
        Double dprice1 = 0.331364;
        Double dprice =  Math.round(dprice1 * 100.0) / 100.0;
        Double kwhPrice = dprice*Double.parseDouble(kwh);
        String kwhInfo = "Total consumption: "+kwh+" kwh";
        String priceKwh = "Total cost: "+kwhPrice+"€";


        //Information about how to generate a PDF from: https://www.baeldung.com/java-pdf-creation

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage\\invoice_customerID_"+customerID+".pdf"));

        document.open();
        //set font for pdf
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        //creat paragraph for pdf
        Paragraph para = new Paragraph("Creation Time: "+timestamp + "\n" + customerInfo + "\n" +price+ "\n" +  kwhInfo + "\n" + priceKwh, font);

        document.add(para);
        document.close();


        System.out.println("PDF for "+customerInfo+" generated");
    }

   //gathers customer information from the customer DB
    public Customer getCutomerInfo(String customerId){

        String query = "SELECT * FROM customer WHERE id = "+customerId;

        Customer customer = new Customer(0, "first_name", "last_name");

        try (
                Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                //gather information and set customer information
                int id = rs.getInt("id");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");

                customer.setId(id);
                customer.setFirst_name(first_name);
                customer.setLast_name(last_name);

            }

        } catch (SQLException e) {
            System.out.println("Error getCustomerInfo: "+e.getMessage());
        }
        return customer;
    }

    //starts the Consumer to get the messages from the DataCollectionReceiver
    public void startWork(){
        try {
            rabbitmq.recv("yellow");
        } catch (IOException | TimeoutException e) {
            System.out.println("Error startWork PDFGenerator: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
