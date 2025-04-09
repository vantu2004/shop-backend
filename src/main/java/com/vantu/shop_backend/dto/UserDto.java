package com.vantu.shop_backend.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserDto {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private ImageDto image;
	private CartDto cart;
	private Set<OrderDto> orders;
}
