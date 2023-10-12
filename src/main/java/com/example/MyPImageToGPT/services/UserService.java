package com.example.MyPImageToGPT.services;

import com.example.MyPImageToGPT.user.User;
import com.example.MyPImageToGPT.repostories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public User updateUserFields(Integer userId, User updatedUser) {
        User existingUser = findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        // Add other fields as necessary

        return userRepository.save(existingUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
