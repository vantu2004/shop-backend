package com.vantu.shop_backend.response;

import com.vantu.shop_backend.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {
	private UserDto user;
	private String accessToken;
	private String refreshToken;
}
