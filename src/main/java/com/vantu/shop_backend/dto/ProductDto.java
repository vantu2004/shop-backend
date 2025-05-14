package com.vantu.shop_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ProductDto {
	private Long id;
	private String name;
	private String brand;
	private BigDecimal price;
	private int inventory;
	private int soldCount;
	private String description;
	private LocalDateTime dateAdded;
	private CategoryDto category;
	private List<ImageDto> images;
}
