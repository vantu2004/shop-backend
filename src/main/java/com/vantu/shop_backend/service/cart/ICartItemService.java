package com.vantu.shop_backend.service.cart;

import com.vantu.shop_backend.model.CartItem;

public interface ICartItemService {
	void addItemToCart(Long cartId, Long productId, int quantity);

	void deleteItemFromCart(Long cartId, Long ProductId);

	void updateItemQuantity(Long cartId, Long productId, int quantity);

	CartItem getCartItem(Long cartId, Long ProductId);
}
