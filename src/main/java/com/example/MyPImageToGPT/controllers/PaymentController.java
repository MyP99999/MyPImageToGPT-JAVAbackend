package com.example.MyPImageToGPT.controllers;

import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.dto.PaymentRequest;
import com.example.MyPImageToGPT.jwt.JwtService;
import com.example.MyPImageToGPT.services.UserService;
import com.example.MyPImageToGPT.user.UserDetailImp;
import com.example.MyPImageToGPT.user.UserDetailServiceImp;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:3000")
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService; // Autowire JwtService

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount())
                .setCurrency("ron")
                .addPaymentMethodType("card")
                .build();
        System.out.println(paymentRequest);
        try {
            PaymentIntent intent = PaymentIntent.create(params);

            // Retrieve user and update token balance
            Optional<User> userOptional = userService.findById(paymentRequest.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                int tokensToAdd = calculateTokens(paymentRequest.getAmount());
                userService.updateTokenBalance(user, tokensToAdd);


                var response = new Object() {
                    public final String clientSecret = intent.getClientSecret();
                };
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private int calculateTokens(long amount) {
        // Implement logic to calculate tokens based on the amount
        // For example, 1 token per 100 currency units
        return (int) (amount);
    }
}
