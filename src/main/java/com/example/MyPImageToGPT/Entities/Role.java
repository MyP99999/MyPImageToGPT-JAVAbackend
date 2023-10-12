package com.example.MyPImageToGPT.Entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name; // Name of the role, e.g., "ADMIN", "USER", etc.

    //... other fields if needed

    //... constructors, getters, setters, etc. (Lombok annotations above should take care of these)
}
