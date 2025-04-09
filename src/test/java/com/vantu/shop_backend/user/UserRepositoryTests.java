package com.vantu.shop_backend.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.vantu.shop_backend.model.Role;
import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.repository.RoleRepository;
import com.vantu.shop_backend.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Test
	public void testCreateAdminAndUsers() {
	    // Lấy role từ database
	    Role adminRole = this.roleRepository.findByName("ADMIN");
	    Role userRole = this.roleRepository.findByName("USER");

	    List<User> users = new ArrayList<>();

	    // 1 Admin
	    users.add(User.builder()
	            .firstName("Admin")
	            .lastName("System")
	            .email("admin@example.com")
	            .password("123456")
	            .roles(Set.of(adminRole))
	            .build());

	    // 5 Users
	    users.add(User.builder()
	            .firstName("Nguyen")
	            .lastName("An")
	            .email("nguyen.an@example.com")
	            .password("123456")
	            .roles(Set.of(userRole))
	            .build());

	    users.add(User.builder()
	            .firstName("Tran")
	            .lastName("Binh")
	            .email("tran.binh@example.com")
	            .password("123456")
	            .roles(Set.of(userRole))
	            .build());

	    users.add(User.builder()
	            .firstName("Le")
	            .lastName("Chi")
	            .email("le.chi@example.com")
	            .password("123456")
	            .roles(Set.of(userRole))
	            .build());

	    users.add(User.builder()
	            .firstName("Pham")
	            .lastName("Dung")
	            .email("pham.dung@example.com")
	            .password("123456")
	            .roles(Set.of(userRole))
	            .build());

	    users.add(User.builder()
	            .firstName("Hoang")
	            .lastName("Em")
	            .email("hoang.em@example.com")
	            .password("123456")
	            .roles(Set.of(userRole))
	            .build());

	    List<User> savedUsers = userRepository.saveAll(users);

	    assertThat(savedUsers).hasSize(6);
	    assertThat(savedUsers).anyMatch(u -> u.getRoles().contains(adminRole));
	    assertThat(savedUsers).filteredOn(u -> u.getRoles().contains(userRole)).hasSize(5);
	}

}
