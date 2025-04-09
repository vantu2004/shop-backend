package com.vantu.shop_backend.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.vantu.shop_backend.model.Category;
import com.vantu.shop_backend.model.Product;
import com.vantu.shop_backend.repository.CategoryRepository;
import com.vantu.shop_backend.repository.ProductRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Test
	public void testCreateProductsForEachCategory() {
	    List<Category> categories = categoryRepository.findAll();
	    assertThat(categories).hasSize(10); // Đảm bảo có 10 Category trong DB

	    List<Product> products = new ArrayList<>();

	    for (Category category : categories) {
	        Product product1 = Product.builder()
	                .name("Sản phẩm 1 - " + category.getName())
	                .brand("Samsung")
	                .price(BigDecimal.valueOf(4990000))
	                .inventory(100)
	                .description("Mô tả sản phẩm 1 thuộc " + category.getName())
	                .category(category)
	                .build();

	        Product product2 = Product.builder()
	                .name("Sản phẩm 2 - " + category.getName())
	                .brand("Apple")
	                .price(BigDecimal.valueOf(7990000))
	                .inventory(50)
	                .description("Mô tả sản phẩm 2 thuộc " + category.getName())
	                .category(category)
	                .build();

	        products.add(product1);
	        products.add(product2);
	    }

	    // Lưu từng batch nếu danh sách lớn
	    List<Product> savedProducts = productRepository.saveAll(products);

	    assertThat(savedProducts).hasSize(20);
	    assertThat(savedProducts).allMatch(p -> p.getId() != null && p.getCategory() != null);
	}
}
