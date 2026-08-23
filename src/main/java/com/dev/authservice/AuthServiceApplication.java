package com.dev.authservice;

import com.dev.authservice.dtos.UserDto;
import com.dev.authservice.models.Role;
import com.dev.authservice.models.User;
import com.dev.authservice.respositories.RoleRepository;
import com.dev.authservice.respositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class AuthServiceApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public AuthServiceApplication(UserRepository userRepository,
					   RoleRepository roleRepository,
					   BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {


		String adminEmail = "admin@commercecore";

		if (userRepository.findByEmail(adminEmail).isPresent()) {
			System.out.println("Admin already exists, skipping seed");
			return;
		}

		User admin = new User();
		admin.setEmail(adminEmail);
		admin.setPassword(bCryptPasswordEncoder.encode("admin@password"));

		Role adminRole = roleRepository.findByRole("ADMIN")
				.orElseGet(() -> {
					Role newRole = new Role();
					newRole.setRole("ADMIN");
					return newRole;
				});

		admin.setRoles(new HashSet<>(Set.of(adminRole)));

		userRepository.save(admin);
		System.out.println("Seeded admin user: " + adminEmail);

	}
}
