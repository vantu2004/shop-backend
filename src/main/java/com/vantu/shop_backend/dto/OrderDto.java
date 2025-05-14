package com.vantu.shop_backend.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import com.vantu.shop_backend.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderDto {
	private Long id;
	private Date orderDate;
	private OrderStatus orderStatus;
	private BigDecimal totalAmount;
	private String address;
	private Set<OrderItemDto> orderItems;
	private BranchDto branch;
	private String paymentMethod;
	private String cardType;
}
