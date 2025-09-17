package com.dev.authservice.dtos;

import com.dev.authservice.models.Role;
import com.dev.authservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class UserDto{

    private String email;

    private Set<Role> roleList = new HashSet<>();

    public static UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoleList(user.getRoles());
        return userDto;
    }

}
