package com.example.MyPImageToGPT.controllers;

import com.example.MyPImageToGPT.services.UserService;
import com.example.MyPImageToGPT.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Integer id){
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User with ID " + id + " has been deleted.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateById(@PathVariable Integer id, @RequestBody User updatedUser) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        updatedUser.setId(id);  // Make sure the ID in the updatedUser object matches the path variable ID
        userService.save(updatedUser);
        return ResponseEntity.ok("User with ID " + id + " has been updated.");
    }

    // ... other endpoints if needed
}
