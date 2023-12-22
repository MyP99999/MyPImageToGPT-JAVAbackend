package com.example.MyPImageToGPT.controllers;

import com.example.MyPImageToGPT.services.OCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class OCRController {

    @Autowired
    private OCRService ocrService;

    @PostMapping("/ocr")
    public ResponseEntity<String> readTextFromImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded or file is empty");
        }
        try {
            String ocrResult = ocrService.doOCR(file);
            return ResponseEntity.ok(ocrResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file");
        }
    }

}
