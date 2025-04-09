package com.vantu.shop_backend.role;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.vantu.shop_backend.model.Role;
import com.vantu.shop_backend.repository.RoleRepository;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	@Autowired
	private RoleRepository roleRepository;

	@Test
	public void testCreateRoles() {
		List<Role> roles = new ArrayList<>();

		roles.add(Role.builder().name("ADMIN").build());
		roles.add(Role.builder().name("USER").build());

		List<Role> savedRoles = this.roleRepository.saveAll(roles);
		
		assertThat(savedRoles).isNotEmpty();
	}

}
