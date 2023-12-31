package com.example.MyPImageToGPT.controllers;

import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.auth.AuthenticationRequest;
import com.example.MyPImageToGPT.auth.AuthenticationResponse;
import com.example.MyPImageToGPT.auth.RegisterRequest;
import com.example.MyPImageToGPT.dto.PasswordResetRequest;
import com.example.MyPImageToGPT.jwt.JwtService;
import com.example.MyPImageToGPT.responses.MessageResponse;
import com.example.MyPImageToGPT.services.AuthenticationService;
import com.example.MyPImageToGPT.services.UserService;
import com.example.MyPImageToGPT.user.UserDetailServiceImp;
import com.example.MyPImageToGPT.validations.RegisterValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class AuthenticationController {

    private final AuthenticationService service;
    private final RegisterValidation registerValidation;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailServiceImp userDetailServiceImp;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request)
    {
        ResponseEntity<Object> validationResponse = registerValidation.validateRegistration(request);
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: All fields are required!"));
        }else if (validationResponse != null)
        {
            return validationResponse;
        }

        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(service.authenticate(request));
        } catch (UsernameNotFoundException e) {
            // User not found
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: No user found with the provided email."));
        } catch (BadCredentialsException e) {
            // Invalid username or password
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid email or password!"));
        } catch (AuthenticationException e) {
            // Generic authentication failure
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: The account needs to be activated to login."));
        }
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailServiceImp.loadUserByName(username);

            if (jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
                String newJwtToken = jwtService.generateToken(userDetails);
                return ResponseEntity.ok(new AuthenticationResponse(newJwtToken, null, null));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during token refresh: " + e.getMessage());
        }
    }
    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestParam("code") String code) {
        try {
            AuthenticationResponse response = service.authenticateWithGoogle(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exceptions (like failed token exchange, user creation, etc.)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during Google authentication: " + e.getMessage());
        }
    }

    @PostMapping("/googleNative")
    public ResponseEntity<?> authenticateWithGoogleNative(@RequestParam("code") String code) {
        try {
            AuthenticationResponse response = service.authenticateWithGoogleNative(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exceptions (like failed token exchange, user creation, etc.)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during Google authentication: " + e.getMessage());
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam String token) {
        Optional<User> userOptional = userService.findByActivationToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(true);
            user.setActivationToken(null); // Clear the token
            userService.save(user);
            return ResponseEntity.ok("Account activated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation token");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            service.forgotPassword(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent successfully."));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during password reset: " + e.getMessage());
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            service.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new MessageResponse("Password reset successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during password reset: " + e.getMessage());
        }
    }

}
