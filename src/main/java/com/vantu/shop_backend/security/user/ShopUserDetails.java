package com.vantu.shop_backend.security.user;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vantu.shop_backend.model.User;

public class ShopUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private User user;

	public ShopUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub

		/*
		 * SimpleGrantedAuthority là class kế thừa GrantedAuthority đại diện cho 1 role
		 * của 1 user, 1 user có thể có nhiều role nên dùng list để lấy hết role
		 */
		List<SimpleGrantedAuthority> authorities = this.user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.user.getEmail();
	}

	// dùng để tạo jwt bên class JwtUtils
	public Long getId() {
		return this.user.getId();
	}
}
