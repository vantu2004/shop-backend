package com.vantu.shop_backend.service.order;

import java.util.List;

import com.vantu.shop_backend.dto.OrderDto;
import com.vantu.shop_backend.exceptions.CouldNotCancelOrder;

public interface IOrderService {
	OrderDto placeOrder(Long userId, Long branchId, String address);

	OrderDto getOrder(Long orderId);

	List<OrderDto> getOrdersByUser(Long userId);
	
	OrderDto cancelOrder(Long orderId) throws CouldNotCancelOrder;
}
