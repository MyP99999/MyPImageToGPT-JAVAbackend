package com.example.MyPImageToGPT.services;

import com.example.MyPImageToGPT.Entities.Role;
import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.repostories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final RoleService roleService;

    public UserService(RoleService roleService) {
        this.roleService = roleService;
    }

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

    public Optional<User> findByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }


    public User findOrCreateUser(String email, String username, Boolean isExternalAuth, Integer tokens) {
        Optional<User> userOptional = findByEmail(email);

        if (userOptional.isPresent()) {
            return userOptional.get(); // Return the existing user
        } else {
            // Create a new user
            User newUser = new User();

            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setTokens(10);
            newUser.setExternalAuth(isExternalAuth);
            Optional<Role> defaultRole = roleService.findRoleById(1);
            defaultRole.ifPresent(newUser::setRole);
            newUser.setActive(true);


            return userRepository.save(newUser); // Save and return the new user
        }
    }

    public void updateTokenBalance(User user, int additionalTokens) {
        if (user != null) {
            int currentTokens = user.getTokens();
            int updatedTokens = currentTokens + additionalTokens;
            user.setTokens(updatedTokens);

            userRepository.save(user);
        } else {
            // Handle the case where the user is not found
            throw new IllegalStateException("User not found");
        }
    }

    public void substractTokenBalance(User user, int additionalTokens) {
        if (user != null) {
            int currentTokens = user.getTokens();
            int updatedTokens = currentTokens - additionalTokens;
            user.setTokens(updatedTokens);

            userRepository.save(user);
        } else {
            // Handle the case where the user is not found
            throw new IllegalStateException("User not found");
        }
    }

    // In UserService class
    public Optional<User> findByActivationToken(String token) {
        return userRepository.findByActivationToken(token);
    }
}
