package com.ocrrh.ocr.controllers;

import com.ocrrh.ocr.services.EmployeeService;
import com.ocrrh.ocr.services.HRdocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class HRdocumentController {
    @Autowired
    private HRdocumentService hrDocumentService;

    private static final List<String> IMAGE_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png"
    );

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        if (contentType == null || fileName == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file upload");
        }

        try {

            if ("application/pdf".equals(contentType)) {
                String message = hrDocumentService.processPdf(file);
                return new ResponseEntity<>(message, HttpStatus.OK);
            }

            else if (IMAGE_CONTENT_TYPES.contains(contentType)) {
                String message = hrDocumentService.processImage(file);
                return new ResponseEntity<>(message, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Unsupported file type");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }


}
