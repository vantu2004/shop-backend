package com.vantu.shop_backend.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDto {
	private Long id;
	private int quantity;
	private BigDecimal unitPrice;
	private ProductDto product;
}
