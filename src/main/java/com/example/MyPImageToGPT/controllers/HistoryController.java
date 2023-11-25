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
@CrossOrigin("http://localhost:3000")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<History>> getHistoryByUserId(@PathVariable Integer userId) {
        try {
            List<History> historyList = historyService.findHistoryByUserId(userId);
            return new ResponseEntity<>(historyList, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}/{historyID}")
    public ResponseEntity<History> getHistoryById(@PathVariable Integer userId, @PathVariable Integer historyID) {
        try {
            History history = historyService.getHistoryById(userId, historyID);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/user")
    public ResponseEntity<List<History>> getAll() {
        try {
            List<History> historyList = historyService.findAll();
            return new ResponseEntity<>(historyList, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
