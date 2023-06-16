package com.example.restapi;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class DownloadLinkController {

//path to download the pdf with customerID
    //information about creating a path with springboot that starts a download from: https://o7planning.org/11765/spring-boot-file-download
    @GetMapping("/download/{customerID}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String customerID) throws IOException {
        //creat url where the file should be
        String filePath = "C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage\\invoice_customerID_"+customerID+".pdf";

       //create Path Object with url
        Path file = Paths.get(filePath);
        //creat Resource with path
        Resource resource = new UrlResource(file.toUri());

        //check if resource (pdf File) exists
        if (resource.exists()) {
            //if pdf exists
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            //if pdf does not exist
            System.out.println("No PDF for download found");
            return ResponseEntity.notFound().build();
        }
    }
}
