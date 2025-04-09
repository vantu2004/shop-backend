package com.vantu.shop_backend.security.user;

import java.util.Optional;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = Optional.ofNullable(this.userRepository.findByEmail(email))
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		if (!user.isVerified()) {
			/*
			 * Ném ngoại lệ để Spring Security biết tài khoản bị vô hiệu hóa (hoặc chưa xác
			 * thực OTP)
			 */
			throw new DisabledException("Account is not verified yet.");
		}

		return new ShopUserDetails(user);
	}

}
