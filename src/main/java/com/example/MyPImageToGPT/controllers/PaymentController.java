package com.example.MyPImageToGPT.controllers;

import com.example.MyPImageToGPT.dto.PaymentRequest;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:3000")
public class PaymentController {

    @PostMapping("/create-payment-intent")
    public String createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount())
                .setCurrency("ron")
                .addPaymentMethodType("card") // Ensure this matches what's enabled in your Stripe dashboard
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            return intent.getClientSecret();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
