package com.vantu.shop_backend.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.dto.OrderDto;
import com.vantu.shop_backend.enums.OrderStatus;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Cart;
import com.vantu.shop_backend.model.Order;
import com.vantu.shop_backend.model.OrderItem;
import com.vantu.shop_backend.model.Product;
import com.vantu.shop_backend.repository.OrderRepository;
import com.vantu.shop_backend.repository.ProductRepository;
import com.vantu.shop_backend.service.cart.ICartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final ICartService iCartService;

	private final ModelMapper modelMapper;

	@Override
	public OrderDto placeOrder(Long userId) {
		// TODO Auto-generated method stub
		Cart cart = this.iCartService.getCartByUserId(userId);

		Order order = this.createOrder(cart);
		Set<OrderItem> orderItems = this.createOrderItem(order, cart);

		order.setTotalAmount(this.calculateTotalAmount(orderItems));
		order.setOrderItems(orderItems);

		Order savedOrder = this.orderRepository.save(order);

		// sau khi lưu order xong thì xóa cart
		this.iCartService.clearCart(cart.getId());

		return this.convertOrderEntityToOrderDto(savedOrder);
	}

	@Override
	public OrderDto getOrder(Long orderId) {
		// TODO Auto-generated method stub
		return this.orderRepository.findById(orderId).map(this::convertOrderEntityToOrderDto)
				.orElseThrow(() -> new ResourceNotFoundException("Order Not Found!"));
	}

	@Override
	public List<OrderDto> getOrdersByUser(Long userId) {
		return this.orderRepository.findAllByUserId(userId).stream().map(this::convertOrderEntityToOrderDto)
				.collect(Collectors.toList());
	}

	private Order createOrder(Cart cart) {
		Order order = new Order();
		order.setUser(cart.getUser());
		order.setOrderStatus(OrderStatus.PEDDING);
		order.setOrderDate(LocalDate.now());

		return order;
	}

	// dùng set vì trong order dùng set
	private Set<OrderItem> createOrderItem(Order order, Cart cart) {
		return cart.getCartItems().stream().map(cartItem -> {
			Product product = cartItem.getProduct();
			product.setInventory(product.getInventory() - cartItem.getQuantity());

			this.productRepository.save(product);

			return OrderItem.builder().quantity(cartItem.getQuantity()).unitPrice(cartItem.getUnitPrice()).order(order)
					.product(product).build();
		}).collect(Collectors.toSet());
	}

	private BigDecimal calculateTotalAmount(Set<OrderItem> orderItems) {
		return orderItems.stream()
				.map(orderItem -> orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private OrderDto convertOrderEntityToOrderDto(Order order) {
		return this.modelMapper.map(order, OrderDto.class);
	}
}
