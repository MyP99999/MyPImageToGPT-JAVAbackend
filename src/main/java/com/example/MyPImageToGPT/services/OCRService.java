package com.example.MyPImageToGPT.services;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

@Service
public class OCRService {

    public String doOCR(MultipartFile file) {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("E:\\WebDev\\Tesseract\\tessdata");
        tesseract.setLanguage("eng");
        tesseract.setTessVariable("user_defined_dpi", "100");

        try {
            // Convert MultipartFile to BufferedImage
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            // Use the BufferedImage directly for OCR
            return tesseract.doOCR(bufferedImage);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while reading image";
        }
    }
}

