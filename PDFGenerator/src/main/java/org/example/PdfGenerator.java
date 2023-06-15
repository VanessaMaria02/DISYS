package org.example;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.services.rabbitmq;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.*;

public class PdfGenerator {


    public void generatePdf(String message) throws DocumentException, FileNotFoundException {

        String[] parts = message.split(";");

        String customerID = "";
        String kwh = "";

        if(parts.length == 2){
            customerID = parts[0];
            kwh = parts[1];

            System.out.println("PdfGenerator: "+customerID+"kwh: "+kwh);
        }else {
            System.out.println("Ungültiges Format der Message.");
        }

        Customer customer = getCutomerInfo(customerID);

        if(customer.getFirst_name().equals("first_name")){
            customer.setId(Integer.parseInt(customerID));
            customer.setFirst_name("No Customer");
            customer.setLast_name("With this ID");
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String customerinfo = "Customer: "+customer.getId() + " " + customer.getFirst_name()+ " " + customer.getLast_name();
        String kwhInfo = "Total consumption: "+kwh+" kwh";

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage\\invoice_customerID_"+customerID+".pdf"));

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Paragraph para = new Paragraph(timestamp + "\n" + customerinfo + "\n" + kwhInfo, font);

        document.add(para);
        document.close();


        System.out.println("PDF Generated:" +customer + "kwh:"+kwh);
    }

    public Customer getCutomerInfo(String customerId){

        String query = "SELECT * FROM customer WHERE id = "+customerId;

        Customer customer = new Customer(0, "first_name", "last_name");

        try (
                Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");

                customer.setId(id);
                customer.setFirst_name(first_name);
                customer.setLast_name(last_name);
                System.out.println(customer);


            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customer;
    }
}
