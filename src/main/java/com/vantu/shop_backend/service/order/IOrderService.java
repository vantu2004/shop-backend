package com.vantu.shop_backend.service.order;

import java.util.List;

import com.vantu.shop_backend.dto.OrderDto;

public interface IOrderService {
	OrderDto placeOrder(Long userId);

	OrderDto getOrder(Long orderId);

	List<OrderDto> getOrdersByUser(Long userId);
}
