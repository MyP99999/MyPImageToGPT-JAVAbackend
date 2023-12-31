package com.example.MyPImageToGPT.repostories;

import com.example.MyPImageToGPT.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByActivationToken(String activationToken);
    Optional<User> findByResetToken(String resetToken);

}
