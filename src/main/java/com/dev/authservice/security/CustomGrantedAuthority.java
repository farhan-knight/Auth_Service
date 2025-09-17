package com.dev.authservice.security;

import com.dev.authservice.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonDeserialize(as = CustomGrantedAuthority.class)
@NoArgsConstructor
public class CustomGrantedAuthority implements GrantedAuthority {
    private Role role;

    public CustomGrantedAuthority(Role role) {
        this.role = role;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        return role.getRole();
    }
}
