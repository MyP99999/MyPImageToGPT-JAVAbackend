package com.example.MyPImageToGPT.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserDetailServiceImp userDetailsService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        // Extract email or other details from oidcUser
        String email = oidcUser.getEmail();

        // Use email to load or create a user
        UserDetails user = userDetailsService.loadUserByUsername(email);

        // ... additional processing if needed

        return oidcUser;
    }
}
