package com.example.MyPImageToGPT.user;

import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.repostories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email));

        return UserDetailImp.build(user);
    }

    private User createNewUser(String email) {
        // Create a new user entity and set its properties
        User newUser = new User();
        newUser.setEmail(email);
        // Set other properties based on OAuth2 details (if available)

        // Save the new user in the database
        return userRepository.save(newUser);
    }
}
