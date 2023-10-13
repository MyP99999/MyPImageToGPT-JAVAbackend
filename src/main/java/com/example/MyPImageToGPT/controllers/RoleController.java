package com.example.MyPImageToGPT.controllers;

import com.example.MyPImageToGPT.Entities.Role;
import com.example.MyPImageToGPT.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class RoleController {

    @Autowired
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<Iterable<Role>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> findById(@PathVariable Integer id) {
        return roleService.findRoleById(id)
                .map(role -> ResponseEntity.ok(role)) // if role is present, return it with 200 OK status
                .orElseGet(() -> ResponseEntity.notFound().build()); // if role is not present, return 404 Not Found
    }


    //... add other endpoints as needed (like create, update, delete for roles)
}
