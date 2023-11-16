package com.example.MyPImageToGPT.user;

import com.example.MyPImageToGPT.Entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailImp implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final int id;

    private final String username;
    private final String email;
    private final String password;
    private final int tokens;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailImp(Integer id, String username, String email, String password,
                         int tokens, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.tokens = tokens;
        this.authorities = authorities;
    }

    public static UserDetailImp build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String roleName = (user.getRole() != null) ? user.getRole().getName() : "USER";
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

        return new UserDetailImp(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getTokens(),
                authorities
        );
    }
    public String getRole() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.substring(auth.indexOf('_') + 1)) // Extract role name after "ROLE_"
                .collect(Collectors.joining(","));
    }
    // Implement getters for id and email

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserDetailImp other = (UserDetailImp) obj;
        return Objects.equals(id, other.id);
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getTokens() {
        return tokens;
    }
}
