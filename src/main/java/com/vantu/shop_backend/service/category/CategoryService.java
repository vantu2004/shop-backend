package com.vantu.shop_backend.service.category;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.dto.CategoryDto;
import com.vantu.shop_backend.exceptions.AlreadyExistsException;
import com.vantu.shop_backend.exceptions.CategoryNotFoundException;
import com.vantu.shop_backend.model.Category;
import com.vantu.shop_backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;

	@Override
	public Category getCategoryById(Long categoryId) {
		// TODO Auto-generated method stub
		return this.categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("Category Not Found!"));
	}

	@Override
	public Category getCategoryByName(String name) {
		// TODO Auto-generated method stub
		return this.categoryRepository.findByName(name);
	}

	@Override
	public List<Category> getAllCategories() {
		// TODO Auto-generated method stub
		return this.categoryRepository.findAll();
	}

	@Override
	public Category addCategory(Category category) {
		// TODO Auto-generated method stub
		return Optional.ofNullable(category)
				// filter giữ lại giá trị c nếu đk true, ko giữ c nếu false --> throw exception
				.filter(c -> c.getName() != null && !this.categoryRepository.existsByName(c.getName()))
				// "::" viết tắt của lamda, có 2 kiểu (class-method; instance::method)
				.map(this.categoryRepository::save)
				.orElseThrow(() -> new AlreadyExistsException(category.getName() + " already exist!"));
	}

	@Override
	public Category updateCategory(Category category, Long id) {
		// TODO Auto-generated method stub
		return Optional.ofNullable(getCategoryById(id)).map(oldCategory -> {
			oldCategory.setName(category.getName());
			return this.categoryRepository.save(oldCategory);
		}).orElseThrow(() -> new CategoryNotFoundException("Category Not Found!"));
	}

	@Override
	public void deleteCategoryById(Long categoryId) {
		// TODO Auto-generated method stub
		this.categoryRepository.findById(categoryId).ifPresentOrElse(this.categoryRepository::delete, () -> {
			throw new CategoryNotFoundException("Category Not Found!");
		});
	}

	@Override
	public List<CategoryDto> getConvertedCategories(List<Category> categories) {
		return categories.stream().map(this::convertCategoryEntityToCategoryDto).toList();
	}

	@Override
	public CategoryDto convertCategoryEntityToCategoryDto(Category category) {
		CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);
//		List<Image> images = this.imageRepository.findByOwnerIdAndOwnerType(product.getId(), OwnerType.PRODUCT);
//		/**
//		 * Biến đổi List<Image> thành một Stream để xử lý từng phần tử tuần tự.
//		 *
//		 * @stream() Biến List<Image> thành một luồng (stream) để xử lý tuần tự.
//		 * @map() Áp dụng hàm chuyển đổi (mapping function) lên từng phần tử, tạo một
//		 *        Stream mới chứa kết quả sau khi chuyển đổi.
//		 * @toList() Thu thập kết quả từ Stream về thành một danh sách (List).
//		 */
//		List<ImageDto> imageDtos = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).toList();
//
//		productDto.setImages(imageDtos);

		return categoryDto;
	}
}
