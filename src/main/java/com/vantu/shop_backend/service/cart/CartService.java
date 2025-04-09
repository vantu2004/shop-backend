package com.vantu.shop_backend.service.cart;

import java.math.BigDecimal;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.dto.CartDto;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Cart;
import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

	private final CartRepository cartRepository;

	private final ModelMapper modelMapper;

	@Override
	public Cart getCart(Long id) {
		// TODO Auto-generated method stub
		return this.cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart Not Found!"));
	}

	@Override
	public void clearCart(long cartId) {
		this.cartRepository.findById(cartId).ifPresentOrElse(cart -> {
			if (cart.getUser() != null) {
				/*
				 * Gỡ liên kết từ User đến Cart, nếu ko gỡ thì khi gọi clearCart sẽ ko xóa được
				 * cart vì cart và user còn quan hệ với nhau
				 */
				cart.getUser().setCart(null);
			}

			// Làm trống các CartItem để kích hoạt orphanRemoval
			cart.getCartItems().clear();

			/*
			 * Xóa Cart và các CartItem liên quan (CascadeType.ALL và orphanRemoval=true sẽ
			 * tự động xử lý việc xóa CartItem)
			 */
			this.cartRepository.deleteById(cart.getId());
		}, () -> {
			throw new ResourceNotFoundException("Cart Not Found!");
		});
	}

	@Override
	public BigDecimal getTotalPrice(long id) {
		// TODO Auto-generated method stub
		/*
		 * mặc định khi add item vào cart thì bên hàm addItemToCart đã xử lý tính
		 * totalAmount nên chỉ việc get ra
		 */
		return this.getCart(id).getTotalAmount();
	}

	@Override
	public Cart initializeNewCart(User user) {
		return this.cartRepository.findByUserId(user.getId()).orElseGet(() -> {
			Cart cart = new Cart();
			cart.setUser(user);

			return this.cartRepository.save(cart);
		});
	}

	@Override
	public Cart getCartByUserId(Long userId) {
		// TODO Auto-generated method stub
		return this.cartRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart Not Found!"));
	}

	@Override
	public CartDto convertCartEntityToCartDto(Cart cart) {
		return this.modelMapper.map(cart, CartDto.class);
	}
}
