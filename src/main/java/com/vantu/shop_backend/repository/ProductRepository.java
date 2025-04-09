package com.vantu.shop_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vantu.shop_backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	/*
	 * jpa tự hiểu là lấy product dựa vào field category và tự ánh xạ dựa vào
	 * categoryName
	 */
	List<Product> findByCategoryName(String categoryName);

	List<Product> findByBrand(String brandName);

	List<Product> findByCategoryNameAndBrand(String categoryName, String brandName);

	List<Product> findByName(String productName);

	List<Product> findByNameAndBrand(String productName, String brandName);

	Long countByNameAndBrand(String productName, String brandName);

	boolean existsByNameAndBrand(String name, String brand);

}
