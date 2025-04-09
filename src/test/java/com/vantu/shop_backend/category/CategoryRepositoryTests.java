package com.vantu.shop_backend.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.vantu.shop_backend.model.Category;
import com.vantu.shop_backend.repository.CategoryRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	public void testCreate10MobileCategories() {
	    List<Category> categories = List.of(
	            Category.builder().name("Điện thoại").build(),
	            Category.builder().name("Máy tính bảng").build(),
	            Category.builder().name("Phụ kiện").build(),
	            Category.builder().name("Tai nghe Bluetooth").build(),
	            Category.builder().name("Sạc - Cáp").build(),
	            Category.builder().name("Ốp lưng - Bao da").build(),
	            Category.builder().name("Pin dự phòng").build(),
	            Category.builder().name("Đồng hồ thông minh").build(),
	            Category.builder().name("Miếng dán màn hình").build(),
	            Category.builder().name("Thiết bị mạng - Router").build()
	    );

	    List<Category> savedCategories = categoryRepository.saveAll(categories);

	    assertThat(savedCategories).hasSize(10);
	    assertThat(savedCategories).allMatch(c -> c.getId() != null);
	}

	
}
