package com.zapzook.todoapp.security;

import com.zapzook.todoapp.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final Long userId;
    private final String username;
    private final String email;

    public UserDetailsImpl(Long userId, String username, String email) {
        this.user = new User(userId, username, email);
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public UserDetailsImpl(User user) {
        this.user = user;
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한을 부여하지 않음
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
}
