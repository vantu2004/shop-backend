package com.vantu.shop_backend.request;

import java.math.BigDecimal;

import com.vantu.shop_backend.model.Category;

import lombok.Data;

@Data
public class ProductUpdateRequest {
	private Long id;
	private String name;
	private String brand;
	private BigDecimal price;
	private int inventory;
	private String description;
	private Category category;
}
