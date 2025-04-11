package com.vantu.shop_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.shop_backend.dto.CategoryDto;
import com.vantu.shop_backend.exceptions.AlreadyExistsException;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Category;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.service.category.ICategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final ICategoryService iCategoryService;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse> getAllCategories() {
		try {
			List<Category> categories = this.iCategoryService.getAllCategories();
			if (categories == null || categories.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found!", null));
			}

			List<CategoryDto> categoryDtos = this.iCategoryService.getConvertedCategories(categories);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Found!", categoryDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse("Error!", HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addCategories(@RequestBody Category category) {
		try {
			Category addedCategory = this.iCategoryService.addCategory(category);
			CategoryDto categoryDto = this.iCategoryService.convertCategoryEntityToCategoryDto(addedCategory);

			return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Success!", categoryDto));
		} catch (AlreadyExistsException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/category/id/{categoryId}")
	public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long categoryId) {
		try {
			Category category = this.iCategoryService.getCategoryById(categoryId);
			CategoryDto categoryDto = this.iCategoryService.convertCategoryEntityToCategoryDto(category);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Found!", categoryDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/category/name/{categoryName}")
	public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String categoryName) {
		try {
			Category category = this.iCategoryService.getCategoryByName(categoryName);
			CategoryDto categoryDto = this.iCategoryService.convertCategoryEntityToCategoryDto(category);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Found!", categoryDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Category Not Found!", null));
		}
	}

	@DeleteMapping("/category/{categoryId}/delete")
	public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long categoryId) {
		try {
			this.iCategoryService.deleteCategoryById(categoryId);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PutMapping("/category/{categoryId}/update")
	public ResponseEntity<ApiResponse> updateCategory(@RequestBody Category category, @PathVariable Long categoryId) {
		try {
			Category updatedCategory = this.iCategoryService.updateCategory(category, categoryId);
			CategoryDto categoryDto = this.iCategoryService.convertCategoryEntityToCategoryDto(updatedCategory);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Update Success!", categoryDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}
}
