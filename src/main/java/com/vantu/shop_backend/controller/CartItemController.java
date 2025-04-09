package com.vantu.shop_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Cart;
import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.service.cart.ICartItemService;
import com.vantu.shop_backend.service.cart.ICartService;
import com.vantu.shop_backend.service.user.IUserService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/cart-items")
@RequiredArgsConstructor
public class CartItemController {

	private final ICartItemService iCartItemService;
	private final ICartService iCartService;
	private final IUserService iUserService;

	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addItemToCart(@RequestParam Long productId, @RequestParam int quantity) {
		try {

			// nếu cart ch tồn tại thì khởi tạo
			User user = this.iUserService.getAuthenticatedUser();
			Cart cart = this.iCartService.initializeNewCart(user);

			this.iCartItemService.addItemToCart(cart.getId(), productId, quantity);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		} catch (JwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteItemFromCart(@RequestParam Long cartId, @RequestParam Long productId) {
		try {
			this.iCartItemService.deleteItemFromCart(cartId, productId);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PutMapping("/update")
	public ResponseEntity<ApiResponse> updateItemQuantity(@RequestParam Long cartId, @RequestParam Long productId,
			@RequestParam int quantity) {
		try {
			this.iCartItemService.updateItemQuantity(cartId, productId, quantity);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}
}
