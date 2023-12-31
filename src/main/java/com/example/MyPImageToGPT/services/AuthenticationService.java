package com.example.MyPImageToGPT.services;

import com.example.MyPImageToGPT.Entities.Role;
import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.auth.AuthenticationRequest;
import com.example.MyPImageToGPT.auth.AuthenticationResponse;
import com.example.MyPImageToGPT.auth.RegisterRequest;
import com.example.MyPImageToGPT.jwt.JwtService;
import com.example.MyPImageToGPT.user.UserDetailServiceImp;
import com.example.MyPImageToGPT.validations.RegisterValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailServiceImp userDetailServiceImp;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RegisterValidation registerValidation;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private EmailService emailService;
    @Value("${google.oauth2.token-endpoint}")
    private String googleTokenEndpoint;

    @Value("${google.oauth2.userinfo-endpoint}")
    private String googleUserinfoEndpoint;

    @Value("${google.oauth2.client-id}")
    private String clientId;

    @Value("${google.oauth2.client-secret}")
    private String clientSecret;

    @Value("${google.oauth2.redirect-uri}")
    private String redirectUri;

    public AuthenticationResponse register(RegisterRequest request) {
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(encodedPassword);
        newUser.setExternalAuth(false);

        // Generate a token in the register method
        newUser.setActivationToken(UUID.randomUUID().toString());
        newUser.setActive(false);

        Optional<Role> userRole = roleService.findRoleById(1);
        userRole.ifPresent(newUser::setRole);

        userService.save(newUser);
        UserDetails userDetails = userDetailServiceImp.loadUserByUsername(newUser.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        String activationLink = "http://localhost:3000/activate?token=" + newUser.getActivationToken();
        emailService.sendEmail(
                newUser.getEmail(),
                "Registration Confirmation",
                "Thank you for registering. Please click on the below link to activate your account: " + activationLink
        );

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            UserDetails userDetails = userDetailServiceImp.loadUserByUsername(request.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            String jwtToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User with username " + request.getUsername() + " does not exist.");
        }
    }

    public void forgotPassword(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Check if the account is authenticated externally
        if (user.isExternalAuth()) {
            throw new IllegalStateException("Password reset is not available for externally authenticated accounts.");
        }

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userService.save(user);

        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
        emailService.sendEmail(
                email,
                "Password Reset Request",
                "To reset your password, click the link below:\n" + resetLink
        );
    }

    public void resetPassword(String token, String newPassword) {
        User user = userService.findByResetToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid reset token"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetToken(null); // Clear the reset token after successful password reset
        userService.save(user);
    }


    public AuthenticationResponse authenticateWithGoogle(String code) {
        try {
            System.out.println(code);

            String accessToken = exchangeCodeForAccessToken(code);
            System.out.println(accessToken);

            if (accessToken == null || accessToken.isEmpty()) {
                System.out.println("Failed to exchange code for access token.");
                // Handle the error appropriately
                return null; // Or throw an exception
            }


            GoogleUser googleUser = fetchGoogleUserDetails(accessToken);

            User user = userService.findOrCreateUser(googleUser.getEmail(), googleUser.getEmail().split("@")[0], googleUser.isExternalAuth(), googleUser.getTokens());

            UserDetails userDetails = userDetailServiceImp.loadUserByUsernameGoogle(user.getEmail());

            String jwtToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            // Handle the error appropriately
            return null; // Or throw a custom exception
        }
    }

    public AuthenticationResponse authenticateWithGoogleNative(String code) {
        try {
            String accessToken = code;

            if (accessToken == null || accessToken.isEmpty()) {
                System.out.println("Failed to exchange code for access token.");
                // Handle the error appropriately
                return null; // Or throw an exception
            }

            GoogleUser googleUser = fetchGoogleUserDetails(accessToken);

            User user = userService.findOrCreateUser(googleUser.getEmail(), googleUser.getEmail().split("@")[0], googleUser.isExternalAuth(), googleUser.getTokens());

            UserDetails userDetails = userDetailServiceImp.loadUserByUsernameGoogle(user.getEmail());

            String jwtToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            // Handle the error appropriately
            return null; // Or throw a custom exception
        }
    }

    private String exchangeCodeForAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);
        params.add("access_type", "offline");
        params.add("code", code);
        System.out.println("params");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        System.out.println("headers");

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(googleTokenEndpoint, requestEntity, Map.class);
        System.out.println(responseEntity);

        Map<String, Object> responseMap = responseEntity.getBody();
        return responseMap != null ? (String) responseMap.get("access_token") : null;
    }



    private GoogleUser fetchGoogleUserDetails(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUser> response = restTemplate.exchange(googleUserinfoEndpoint, HttpMethod.GET, entity, GoogleUser.class);
        return response.getBody();
    }

    // Static nested class representing the structure of a Google user's data.
    private static class GoogleUser {
        private Integer id;
        private String email;
        private String username;

        private Integer tokens;

        public Integer getTokens() {
            return tokens;
        }

        public void setTokens(Integer tokens) {
            this.tokens = tokens;
        }

        private boolean isExternalAuth;

        public boolean isExternalAuth() {
            return true;
        }

        public void setExternalAuth(boolean externalAuth) {
            isExternalAuth = externalAuth;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
// Getters and setters
    }
}
