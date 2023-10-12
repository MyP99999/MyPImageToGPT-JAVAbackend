package com.example.MyPImageToGPT.repostories;

import com.example.MyPImageToGPT.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // The basic CRUD operations are provided by JpaRepository.

    // If you need custom queries, you can define them here. For example:
    // Optional<Role> findByName(String name);
}
