package com.dev.authservice.security;

import com.dev.authservice.models.Role;
import com.dev.authservice.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonDeserialize(as = CustomUserDetails.class)
public class CustomUserDetails implements UserDetails {


    //    private User user;
    private List<CustomGrantedAuthority> authorities;
    private String password;
    private String username;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CustomUserDetails() {}

    public CustomUserDetails(User user) {
//        this.user = user;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.enabled = true;
        this.credentialsNonExpired = true;
        this.password = user.getPassword();
        this.username = user.getEmail();
        this.userId = user.getId();

        List<CustomGrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (Role role: user.getRoles()) {
            grantedAuthorities.add(new CustomGrantedAuthority(role));
        }

        this.authorities = grantedAuthorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<CustomGrantedAuthority> grantedAuthorities = new ArrayList<>();
//
//        for (Role role: user.getRoles()) {
//            grantedAuthorities.add(new CustomGrantedAuthority(role));
//        }
//
//        return grantedAuthorities;
        return authorities;
    }

    @Override
    public String getPassword() {
//        return user.getHashedPassword();
        return password;
    }

    @Override
    public String getUsername() {
//        return user.getEmail();
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
//    private User user;
//
//    public CustomUserDetails() {}
//
//    public CustomUserDetails(User user) {
//        this.user = user;
//    }
//
//
//    @Override
//    @JsonIgnore
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Set<Role> roles = user.getRoles();
//
//        Collection<CustomGrantedAuthority> customGrantedAuthorities = new ArrayList<>();
//        for (Role role : roles) {
//            customGrantedAuthorities.add(
//                    new CustomGrantedAuthority(role)
//            );
//        }
//        return customGrantedAuthorities;
//    }
//
//    @Override
//    @JsonIgnore
//    public String getPassword() {
//        return user.getPassword();
//    }
//
//    @Override
//    @JsonIgnore
//    public String getUsername() {
//        return user.getEmail();
//    }
//
//    @Override
//    @JsonIgnore
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    @JsonIgnore
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    @JsonIgnore
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    @JsonIgnore
//    public boolean isEnabled() {
//        return true;
//    }
}
