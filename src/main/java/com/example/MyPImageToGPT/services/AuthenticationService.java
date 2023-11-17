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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = userDetailServiceImp.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticateWithGoogle(String code) {
        String accessToken = exchangeCodeForAccessToken(code);
        GoogleUser googleUser = fetchGoogleUserDetails(accessToken);

        User user = userService.findOrCreateUser(googleUser.getEmail(),googleUser.getEmail().split("@")[0],googleUser.isExternalAuth(), googleUser.getTokens());

        UserDetails userDetails = userDetailServiceImp.loadUserByUsername(user.getEmail());

        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String exchangeCodeForAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(googleTokenEndpoint, requestEntity, Map.class);

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
