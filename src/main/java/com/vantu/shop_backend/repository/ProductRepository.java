package com.vantu.shop_backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vantu.shop_backend.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	/*
	 * jpa tự hiểu là lấy product dựa vào field category và tự ánh xạ dựa vào
	 * categoryName
	 */
	List<Product> findByCategoryName(String categoryName);

	List<Product> findByBrand(String brandName);

	List<Product> findByCategoryNameAndBrand(String categoryName, String brandName);

	List<Product> findByNameContainingIgnoreCase(String productName);

	List<Product> findByNameAndBrand(String productName, String brandName);

	Long countByNameAndBrand(String productName, String brandName);

	boolean existsByNameAndBrand(String name, String brand);

	List<Product> findAll(Sort sort);

	List<Product> findAllByOrderByDateAddedAsc();

	List<Product> findAllByOrderByDateAddedDesc();

	List<Product> findAllByOrderByPriceAsc();

	List<Product> findAllByOrderByPriceDesc();

	@Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price")
	List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
}
