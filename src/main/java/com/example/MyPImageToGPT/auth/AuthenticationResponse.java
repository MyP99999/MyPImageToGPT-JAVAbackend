package com.example.MyPImageToGPT.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;
    private String error;

    public static AuthenticationResponse error(String error) {
        return AuthenticationResponse.builder().error(error).build();
    }
}
