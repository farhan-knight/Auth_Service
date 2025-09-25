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
public class CustomGrantedAuthority implements GrantedAuthority {
    //    private Role role;
    private String authority;

    public CustomGrantedAuthority() {}


    public CustomGrantedAuthority(Role role) {
//        this.role = role;
        this.authority = role.getRole();
    }

    @Override
    public String getAuthority() {
//        return role.getName();
        return authority;
    }
}
