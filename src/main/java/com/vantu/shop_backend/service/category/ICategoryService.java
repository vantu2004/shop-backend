package com.vantu.shop_backend.service.category;

import java.util.List;

import com.vantu.shop_backend.model.Category;

public interface ICategoryService {
	Category getCategoryById(Long categoryId);

	Category getCategoryByName(String name);

	List<Category> getAllCategories();

	Category addCategory(Category category);

	Category updateCategory(Category category, Long id);

	void deleteCategoryById(Long categoryId);
}
