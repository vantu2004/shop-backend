package com.vantu.shop_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.vantu.shop_backend.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderDto {
	private Long id;
	private LocalDate orderDate;
	private OrderStatus orderStatus;
	private BigDecimal totalAmount;
	private Set<OrderItemDto> orderItems;
}
