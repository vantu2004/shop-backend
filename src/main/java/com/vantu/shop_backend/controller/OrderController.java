package com.vantu.shop_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.shop_backend.dto.OrderDto;
import com.vantu.shop_backend.exceptions.CouldNotCancelOrder;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.service.order.IOrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

	private final IOrderService iOrderService;

	@PostMapping("/add")
	public ResponseEntity<ApiResponse> placeOrder(@RequestParam Long userId, @RequestParam Long branchId,
			@RequestParam String address, @RequestParam String paymentMethod, @RequestParam String cardType) {
		try {
			OrderDto orderDto = this.iOrderService.placeOrder(userId, branchId, address, paymentMethod, cardType);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", orderDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PutMapping("/cancel")
	public ResponseEntity<ApiResponse> cancelOrder(@RequestParam Long orderId) {
		try {
			OrderDto orderDto = this.iOrderService.cancelOrder(orderId);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", orderDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		} catch (CouldNotCancelOrder e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/order/id/{orderId}")
	public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
		try {
			OrderDto orderDto = this.iOrderService.getOrder(orderId);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", orderDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/userId/{userId}")
	public ResponseEntity<ApiResponse> getOrdersByUser(@PathVariable Long userId) {
		try {
			List<OrderDto> orderDtos = this.iOrderService.getOrdersByUser(userId);
			if (orderDtos == null || orderDtos.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Order Not Found!", null));
			}
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", orderDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
		}
	}
}
