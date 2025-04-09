package com.vantu.shop_backend.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Builder.Default
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Builder.Default
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CartItem> cartItems = new HashSet<CartItem>();

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	// thêm là thêm 1 product mới vào cart chứ ko phải tăng productQuantity lên
	public void addItem(CartItem item) {
		this.cartItems.add(item);
		updateTotalAmount();
	}

	// xóa là xóa 1 product trong cart chứ ko phải giảm productQuantity xuống
	public void removeItem(CartItem item) {
		this.cartItems.remove(item);
		updateTotalAmount();
	}

	// dù cartItems rỗng thì vẫn trả về 0
	public void updateTotalAmount() {
		this.totalAmount = this.cartItems.stream().map(cartItem -> {
			// nếu unitPrice null thì set mặc định là 0
			if (cartItem.getUnitPrice() == null) {
				cartItem.setUnitPrice(BigDecimal.ZERO);
			}

			// mặc định khi add item vào cart thì bên hàm addItemToCart đã thực hiện tính
			// toán totalPrice
			return cartItem.getTotalPrice();
		}).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
