package com.vantu.shop_backend.dto;

import lombok.Data;

@Data
public class CategoryDto {
	private Long id;
	private String name;
	private ImageDto image;
}
