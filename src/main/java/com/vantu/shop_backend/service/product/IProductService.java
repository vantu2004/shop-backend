package com.vantu.shop_backend.service.product;

import java.util.List;

import com.vantu.shop_backend.dto.ProductDto;
import com.vantu.shop_backend.model.Product;
import com.vantu.shop_backend.request.AddProductRequest;
import com.vantu.shop_backend.request.ProductUpdateRequest;

public interface IProductService {
	Product addProduct(AddProductRequest addProductRequest);

	Product getProductById(Long id);

	void deleteProductById(Long id);

	Product updateProduct(ProductUpdateRequest productUpdateRequest, Long productId);

	List<Product> getAllProducts();

	List<Product> getProductByCategoryName(String categoryName);

	List<Product> getProductByBrandName(String brandName);

	List<Product> getProductByCategoryNameAndBrandName(String categoryName, String brandName);

	List<Product> getProductByName(String productName);

	List<Product> getProductByProductNameAndBrandName(String productName, String brandName);

	Long countProductsByBrandName(String productName, String brandName);
	
	List<ProductDto> getConvertedProducts(List<Product> products);
	
	ProductDto convertProductEntityToProductDto(Product product);
}
