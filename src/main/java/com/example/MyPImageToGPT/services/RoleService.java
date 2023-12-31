package com.example.MyPImageToGPT.services;

import com.example.MyPImageToGPT.Entities.Role;
import com.example.MyPImageToGPT.repostories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Iterable<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findRoleById(Integer id) {
        return roleRepository.findById(id);
    }

}
