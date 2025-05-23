package com.vantu.shop_backend.service.cart;

import org.springframework.stereotype.Service;

import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Cart;
import com.vantu.shop_backend.model.CartItem;
import com.vantu.shop_backend.model.Product;
import com.vantu.shop_backend.repository.CartRepository;
import com.vantu.shop_backend.repository.ProductRepository;
import com.vantu.shop_backend.service.product.IProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

	private final CartRepository cartRepository;
	private final ICartService iCartService;
	private final IProductService iProductService;
	private final ProductRepository productRepository;

	@Override
	public void addItemToCart(Long cartId, Long productId, int quantity) {
		// TODO Auto-generated method stub
		Cart cart = this.iCartService.getCart(cartId);
		Product product = this.iProductService.getProductById(productId);

		// đảm bảo ko bị NullPointerException do cartItems bị null
		CartItem cartItem = cart.getCartItems().stream()
				.filter(item -> item != null && item.getProduct().getId().equals(product.getId())).findFirst()
				.orElse(new CartItem());

		// trường hợp item ch đc thêm vào cart trc đó
		if (cartItem.getId() == null) {
			cartItem.setQuantity(quantity);
			cartItem.setUnitPrice(product.getPrice());
			cartItem.setProduct(product);
			cartItem.setCart(cart);
		} else {
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
		}

		cartItem.setTotalPrice();
		cart.addItem(cartItem);

		// cập nhật inventory
		product.setInventory(product.getInventory() - quantity);

		/*
		 * do cơ chế cascade = CascadeType.ALL nên khi thêm cartItem vào cartItems của
		 * cart --> lưu --> tự động lưu cartItem trong db --> ko cần lưu thủ công lần
		 * nữa
		 */
		this.cartRepository.save(cart);
		// this.cartItemRepository.save(cartItem);
		this.productRepository.save(product);
	}

	@Override
	public void deleteItemFromCart(Long cartId, Long productId) {
		// TODO Auto-generated method stub
		Cart cart = this.iCartService.getCart(cartId);
		Product product = this.iProductService.getProductById(productId);

		// truyền cartId thay vì cart vì cần hàm kia để getCartItem (hàm này chỉ nhận
		// cartId)
		CartItem cartItem = getCartItem(cartId, productId);

		cart.removeItem(cartItem);

		// xóa cart-item thì cập nhật lại inventory
		product.setInventory(product.getInventory() + cartItem.getQuantity());

		/*
		 * vì trong cart có orphanRemoval = true nên nên chỉ cần remove list cartItems
		 * và save lại thì mặc định tự động xóa cartItem ko còn đc tham chiếu bởi cart
		 */
		this.cartRepository.save(cart);
		this.productRepository.save(product);
	}

	@Override
	public CartItem getCartItem(Long cartId, Long productId) {
		Cart cart = this.iCartService.getCart(cartId);

		// đảm bảo ko bị NullPointerException do cartItems bị null
		return cart.getCartItems().stream().filter(item -> item != null && item.getProduct().getId().equals(productId))
				.findFirst().orElseThrow(() -> new ResourceNotFoundException("Product Not Found!"));
	}

	@Override
	public void updateItemQuantity(Long cartId, Long productId, int quantity) {
		Cart cart = this.iCartService.getCart(cartId);
		Product product = this.iProductService.getProductById(productId);

		CartItem cartItem = cart.getCartItems().stream()
				.filter(item -> item != null && item.getProduct().getId().equals(productId)).findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Product Not Found!"));

		// cập nhật inventory về ban đầu
		product.setInventory(product.getInventory() + cartItem.getQuantity());

		cartItem.setQuantity(quantity);
		cartItem.setUnitPrice(cartItem.getProduct().getPrice());
		cartItem.setTotalPrice();

		// cập nhật inventory
		product.setInventory(product.getInventory() - quantity);

		cart.updateTotalAmount();

		// Nếu có cascade = CascadeType.ALL thì chỉ cần save Cart
		this.cartRepository.save(cart);
		this.productRepository.save(product);
	}
}
