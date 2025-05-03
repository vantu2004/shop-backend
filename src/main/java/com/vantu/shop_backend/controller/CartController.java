package com.vantu.shop_backend.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.shop_backend.dto.CartDto;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Cart;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.service.cart.ICartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/carts")
@RequiredArgsConstructor
public class CartController {

	private final ICartService iCartService;

	@GetMapping("/cart/id/{cartId}")
	public ResponseEntity<ApiResponse> getCart(@PathVariable Long cartId) {
		try {
			Cart cart = this.iCartService.getCart(cartId);
			CartDto cartDto = this.iCartService.convertCartEntityToCartDto(cart);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", cartDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/cart/user-id/{userId}")
	public ResponseEntity<ApiResponse> getCartByUserId(@PathVariable Long userId) {
		try {
			Cart cart = this.iCartService.getCartByUserId(userId);
			CartDto cartDto = this.iCartService.convertCartEntityToCartDto(cart);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", cartDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@DeleteMapping("/cart/{cartId}/clear")
	public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
		try {
			this.iCartService.clearCart(cartId);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/cart/total-amount/{cartId}")
	public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable Long cartId) {
		try {
			BigDecimal totalAmount = this.iCartService.getTotalPrice(cartId);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", totalAmount));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}
}
