package com.dev.authservice.services;

import com.dev.authservice.dtos.UserDto;
import com.dev.authservice.models.Role;
import com.dev.authservice.models.User;
import com.dev.authservice.respositories.RoleRepository;
import com.dev.authservice.respositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserDto getUserDetails(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }
        return UserDto.from(optionalUser.get());
    }

    public UserDto setUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return null;
        }

        List<Role> roles = roleRepository.findAllByIdIn(roleIds);
        User user = userOptional.get();
        user.setRoles(new HashSet<>(roles));
        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }
}