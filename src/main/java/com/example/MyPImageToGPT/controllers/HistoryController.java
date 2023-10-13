package com.example.MyPImageToGPT.controllers;

import com.example.MyPImageToGPT.Entities.History;
import com.example.MyPImageToGPT.services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<History>> getHistoryByUserId(@PathVariable Integer userId) {
        try {
            List<History> historyList = historyService.findHistoryByUserId(userId);
            return new ResponseEntity<>(historyList, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Log error...
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<History>> getAll() {
        try {
            System.out.println("Getting all history...");
            List<History> historyList = historyService.findAll();
            System.out.println("Found {} history items");
            return new ResponseEntity<>(historyList, HttpStatus.OK);
        } catch (RuntimeException e) {
            System.out.println("Error fetching history");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
