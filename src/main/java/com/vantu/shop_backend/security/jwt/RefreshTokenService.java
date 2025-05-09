package com.vantu.shop_backend.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.RefreshToken;
import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.repository.RefreshTokenRepository;
import com.vantu.shop_backend.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private UserRepository userRepository;

	public RefreshToken createRefreshToken(String email) {
		User user = this.userRepository.findByEmail(email);
		if (user == null) {
			throw new ResourceNotFoundException("User not found with email: " + email);
		}

		RefreshToken refreshToken = RefreshToken.builder().user(user).refreshToken(UUID.randomUUID().toString())
				// hạn là 3h
				.expiryDate(Instant.now().plus(3, ChronoUnit.HOURS)).build();

		RefreshToken existingToken = this.refreshTokenRepository.findByUserId(user.getId());
		if (existingToken != null) {
			existingToken.setRefreshToken(refreshToken.getRefreshToken());
			existingToken.setExpiryDate(refreshToken.getExpiryDate());
			return this.refreshTokenRepository.save(existingToken);
		}

		return this.refreshTokenRepository.save(refreshToken);
	}

	public RefreshToken getRefreshToken(String refreshToken) {
		return this.refreshTokenRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new ResourceNotFoundException("Refresh Token Not Found!"));
	}

	public boolean verifyExpiration(RefreshToken refreshToken) {
		if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
			this.refreshTokenRepository.delete(refreshToken);
			return true;
		}
		return false;
	}

	public void deleteByToken(String refreshToken) {
		// TODO Auto-generated method stub
		RefreshToken ref = this.refreshTokenRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new ResourceNotFoundException("Token Not Found!"));
		this.refreshTokenRepository.delete(ref);
	}

}
