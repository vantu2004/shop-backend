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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.shop_backend.dto.ProductDto;
import com.vantu.shop_backend.exceptions.AlreadyExistsException;
import com.vantu.shop_backend.exceptions.ProductNotFoundException;
import com.vantu.shop_backend.model.Product;
import com.vantu.shop_backend.request.AddProductRequest;
import com.vantu.shop_backend.request.ProductUpdateRequest;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.service.product.IProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
	private final IProductService iProductService;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse> getAllProducts() {
		try {
			List<Product> products = this.iProductService.getAllProducts();
			if (products == null || products.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found!", null));
			}

			List<ProductDto> productDtos = this.iProductService.getConvertedProducts(products);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", productDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/product/id/{productId}")
	public ResponseEntity<ApiResponse> getProductId(@PathVariable Long productId) {
		try {
			Product product = this.iProductService.getProductById(productId);
			ProductDto productDto = this.iProductService.convertProductEntityToProductDto(product);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", productDto));
		} catch (ProductNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest addProductRequest) {
		try {
			Product product = this.iProductService.addProduct(addProductRequest);
			ProductDto productDto = this.iProductService.convertProductEntityToProductDto(product);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Success!", productDto));
		} catch (AlreadyExistsException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PutMapping("/product/{productId}/update")
	public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest,
			@PathVariable Long productId) {
		try {
			Product product = this.iProductService.updateProduct(productUpdateRequest, productId);
			ProductDto productDto = this.iProductService.convertProductEntityToProductDto(product);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", productDto));
		} catch (ProductNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@DeleteMapping("/product/{productId}/delete")
	public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long productId) {
		try {
			this.iProductService.deleteProductById(productId);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (ProductNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/by/productname-and-brandname")
	public ResponseEntity<ApiResponse> getProductByNameAndBrand(@RequestParam String productName,
			@RequestParam String brandName) {
		try {
			List<Product> products = this.iProductService.getProductByProductNameAndBrandName(productName, brandName);
			if (products == null || products.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found!", null));
			}

			List<ProductDto> productDtos = this.iProductService.getConvertedProducts(products);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", productDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/by/categoryname-and-brandname")
	public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(@RequestParam String categoryName,
			@RequestParam String brandName) {
		try {
			List<Product> products = this.iProductService.getProductByCategoryNameAndBrandName(categoryName, brandName);
			if (products == null || products.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found!", null));
			}

			List<ProductDto> productDtos = this.iProductService.getConvertedProducts(products);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", productDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/product/name/{productName}")
	public ResponseEntity<ApiResponse> getProductByName(@PathVariable String productName) {
		try {
			List<Product> products = this.iProductService.getProductByName(productName);
			if (products == null || products.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found!", null));
			}

			List<ProductDto> productDtos = this.iProductService.getConvertedProducts(products);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", productDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/brand/name/{brandName}")
	public ResponseEntity<ApiResponse> getProductByBrand(@PathVariable String brandName) {
		try {
			List<Product> products = this.iProductService.getProductByBrandName(brandName);

			if (products == null || products.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found!", null));
			}

			List<ProductDto> productDtos = this.iProductService.getConvertedProducts(products);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", productDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/category/name/{categoryName}")
	public ResponseEntity<ApiResponse> getProductByCategory(@PathVariable String categoryName) {
		try {
			List<Product> products = this.iProductService.getProductByCategoryName(categoryName);

			if (products == null || products.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found!", null));
			}

			List<ProductDto> productDtos = this.iProductService.getConvertedProducts(products);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", productDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/count")
	public ResponseEntity<ApiResponse> countProductsByBrand(@RequestParam String productName,
			@RequestParam String brandName) {
		try {
			Long count = this.iProductService.countProductsByBrandName(productName, brandName);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", count));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}
}
