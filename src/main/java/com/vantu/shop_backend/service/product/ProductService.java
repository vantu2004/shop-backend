package com.vantu.shop_backend.service.product;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.dto.ImageDto;
import com.vantu.shop_backend.dto.ProductDto;
import com.vantu.shop_backend.exceptions.AlreadyExistsException;
import com.vantu.shop_backend.exceptions.ProductNotFoundException;
import com.vantu.shop_backend.model.Category;
import com.vantu.shop_backend.model.Image;
import com.vantu.shop_backend.model.Product;
import com.vantu.shop_backend.repository.CategoryRepository;
import com.vantu.shop_backend.repository.ImageRepository;
import com.vantu.shop_backend.repository.ProductRepository;
import com.vantu.shop_backend.request.AddProductRequest;
import com.vantu.shop_backend.request.ProductUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
/*
 * @RequiredArgsConstructor là một annotation của Lombok, tự động tạo
 * constructor chỉ chứa các thuộc tính "FINAL" (lưu ý) hoặc @NonNull trong
 * class. Dependency injection kiểu constructor
 */
@RequiredArgsConstructor
public class ProductService implements IProductService {

	// thuộc tính final
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ImageRepository imageRepository;

	/*
	 * trong các hàm trả Product, trong Product có listImages, trong Image lại có
	 * image (BLOB) ko thể convert và in như thường đc --> dùng ModelMapper tự động
	 * convert từ entity Image sang DTO ImageDto. Sau khi thêm dependency thì cấu
	 * hình bên ShopConfig để định nghĩa bean cho ModelMapper vì bản thân nó ko phải
	 * là 1 bean
	 */
	private final ModelMapper modelMapper;

	@Override
	public Product addProduct(AddProductRequest addProductRequest) {
		
		if (isExistProduct(addProductRequest.getName(), addProductRequest.getBrand())) {
			throw new AlreadyExistsException("Product Already Exists, You May Update This Instead.");
		}
		
		/*
		 * tìm category, nếu có thì createProduct, nếu ko thì tạo mới category và
		 * createProduct
		 */
		Category category = Optional
				.ofNullable(this.categoryRepository.findByName(addProductRequest.getCategory().getName()))
				// orElseGet() chỉ chạy khi giá trị trong hàm ofNullable rỗng
				.orElseGet(() -> {
					Category newCategory = Category.builder().name(addProductRequest.getCategory().getName()).build();
					return this.categoryRepository.save(newCategory);
				});

		return this.productRepository.save(createProduct(addProductRequest, category));
	}

	public boolean isExistProduct(String name, String brand) {
		return this.productRepository.existsByNameAndBrand(name, brand);
	}

	public Product createProduct(AddProductRequest addProductRequest, Category category) {
		Product product = new Product();
		product.setName(addProductRequest.getName());
		product.setBrand(addProductRequest.getBrand());
		product.setPrice(addProductRequest.getPrice());
		product.setInventory(addProductRequest.getInventory());
		product.setDescription(addProductRequest.getDescription());
		product.setCategory(category);

		return product;
	}

	@Override
	public Product getProductById(Long id) {
		// TODO Auto-generated method stub
		return this.productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product Not Found!"));
	}

	@Override
	public void deleteProductById(Long id) {
		// TODO Auto-generated method stub
		// ifPresentOrElse xử lý kết quả có/ko có của Optional, chỉ thực thi hành động
		this.productRepository.findById(id).ifPresentOrElse(product -> this.productRepository.deleteById(id), () -> {
			throw new ProductNotFoundException("Product Not Found!");
		});
	}

	@Override
	public Product updateProduct(ProductUpdateRequest productUpdateRequest, Long productId) {
		// map xử lý giá trị của optional, có xử lý hành động và trả về giá trị
		return this.productRepository.findById(productId).map(existingProduct -> {
			Product product = updateExistingProduct(existingProduct, productUpdateRequest);
			return this.productRepository.save(product);
		}).orElseThrow(() -> new ProductNotFoundException("Product Not Found!"));
	}

	public Product updateExistingProduct(Product existingProduct, ProductUpdateRequest productUpdateRequest) {
		existingProduct.setName(productUpdateRequest.getName());
		existingProduct.setBrand(productUpdateRequest.getBrand());
		existingProduct.setPrice(productUpdateRequest.getPrice());
		existingProduct.setInventory(productUpdateRequest.getInventory());
		existingProduct.setDescription(productUpdateRequest.getDescription());

		Category category = this.categoryRepository.findByName(productUpdateRequest.getCategory().getName());
		existingProduct.setCategory(category);

		return existingProduct;
	}

	@Override
	public List<Product> getAllProducts() {
		// TODO Auto-generated method stub
		return this.productRepository.findAll();
	}

	@Override
	public List<Product> getProductByCategoryName(String categoryName) {
		// TODO Auto-generated method stub
		return this.productRepository.findByCategoryName(categoryName);
	}

	@Override
	public List<Product> getProductByBrandName(String brandName) {
		// TODO Auto-generated method stub
		return this.productRepository.findByBrand(brandName);
	}

	@Override
	public List<Product> getProductByCategoryNameAndBrandName(String categoryName, String brandName) {
		// TODO Auto-generated method stub
		return this.productRepository.findByCategoryNameAndBrand(categoryName, brandName);
	}

	@Override
	public List<Product> getProductByName(String productName) {
		// TODO Auto-generated method stub
		return this.productRepository.findByName(productName);
	}

	@Override
	public List<Product> getProductByProductNameAndBrandName(String productName, String brandName) {
		// TODO Auto-generated method stub
		return this.productRepository.findByNameAndBrand(productName, brandName);
	}

	@Override
	public Long countProductsByBrandName(String productName, String brandName) {
		// TODO Auto-generated method stub
		return this.productRepository.countByNameAndBrand(productName, brandName);
	}

	@Override
	public List<ProductDto> getConvertedProducts(List<Product> products) {
		return products.stream().map(this::convertProductEntityToProductDto).toList();
	}

	@Override
	public ProductDto convertProductEntityToProductDto(Product product) {
		ProductDto productDto = this.modelMapper.map(product, ProductDto.class);
		List<Image> images = this.imageRepository.findByProductId(product.getId());
		/**
		 * Biến đổi List<Image> thành một Stream để xử lý từng phần tử tuần tự.
		 *
		 * @stream() Biến List<Image> thành một luồng (stream) để xử lý tuần tự.
		 * @map() Áp dụng hàm chuyển đổi (mapping function) lên từng phần tử, tạo một
		 *        Stream mới chứa kết quả sau khi chuyển đổi.
		 * @toList() Thu thập kết quả từ Stream về thành một danh sách (List).
		 */
		List<ImageDto> imageDtos = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).toList();

		productDto.setImages(imageDtos);

		return productDto;
	}
}
