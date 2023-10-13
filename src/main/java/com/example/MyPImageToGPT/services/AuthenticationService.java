package com.example.MyPImageToGPT.services;

import com.example.MyPImageToGPT.Entities.Role;
import com.example.MyPImageToGPT.auth.AuthenticationRequest;
import com.example.MyPImageToGPT.auth.AuthenticationResponse;
import com.example.MyPImageToGPT.auth.RegisterRequest;
import com.example.MyPImageToGPT.jwt.JwtService;
import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.user.UserDetailServiceImp;
import com.example.MyPImageToGPT.validations.RegisterValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailServiceImp userDetailServiceImp;
    private final RoleService roleService;  // Add this

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])([A-Za-z\\d@#$%^&+=!]{10,})$";

    @Autowired
    private RegisterValidation registerValidation;

    public AuthenticationResponse register(RegisterRequest request) {


        User newUser = new User();

        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(encodedPassword);

        Optional<Role> userRole = roleService.findRoleById(1);
        userRole.ifPresent(newUser::setRole); // Ensure this setter exists in your User entity

        // Save the new user to the database
        userService.save(newUser);

        // Authenticate the new user
        UserDetails userDetails = userDetailServiceImp.loadUserByUsername(newUser.getUsername());

        // Generate a JWT token for the new user
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailServiceImp.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
