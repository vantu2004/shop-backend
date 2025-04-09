package com.vantu.shop_backend.service.cart;

import java.math.BigDecimal;

import com.vantu.shop_backend.dto.CartDto;
import com.vantu.shop_backend.model.Cart;
import com.vantu.shop_backend.model.User;

public interface ICartService {
	Cart getCart(Long id);

	void clearCart(long id);

	BigDecimal getTotalPrice(long id);

	Cart initializeNewCart(User user);
	
	Cart getCartByUserId(Long userId);

	CartDto convertCartEntityToCartDto(Cart cart);
}
