package com.vantu.shop_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtRepsonse {
	private Long userId;
	private String accessToken;
	private String refreshToken;
}
