package com.vantu.shop_backend.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vantu.shop_backend.security.user.ShopUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//	kế thừa OncePerRequestFilter để đảm bảo mỗi request chỉ xác thực qua filter 1 lần
@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private ApplicationContext context;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		/*
		 * lấy header Authorization từ request, biến authHeader sẽ chứa token từ header
		 * nếu có
		 */
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String userName = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			// nếu authHeader chứa token thì loại bỏ tiền tố "Bearer "
			token = authHeader.substring(7);
			/*
			 * lấy userName từ token, token đã encode nên cần hàm khác để decode và lấy
			 * thông tin
			 */
			userName = jwtService.extractUserName(token);
		}

		/*
		 * vì JwtFilter xảy ra trc UsernamePasswordAuthenticationFilter nên nếu chưa xác
		 * thực người dùng trc đó thì phải thực hiện xác thực để lưu thông tin vào
		 * SecurityContextHolder
		 */
		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			/*
			 * lấy thông tin user bằng cách gọi loadUserByUsername trong bean
			 * ShopUserDetailsService lấy từ ApplicationContext
			 */
			UserDetails userDetails = context.getBean(ShopUserDetailsService.class).loadUserByUsername(userName);
			/*
			 * trường hợp validate thất bại, đoạn xử lý bên trong sẽ không set
			 * Authentication vào SecurityContextHolder
			 */
			if (jwtService.validateToken(token, userDetails)) {
				/*
				 * nếu xác thực token vs user thành công thì tạo 1 token mới và set vào
				 * SecurityContextHolder để phục vụ cho UsernamePasswordAuthenticationFilter sau
				 * khi JwtFilter hoàn thành
				 */
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			} 
			/*
			 * trường hợp token ko hợp lệ (userName sai hoặc token hết hạn) thì ném
			 * exception để bên JwtAuthEntryPoint bắt và xử lý
			 */			
			else {
				throw new BadCredentialsException("Invalid token");
			}
		}
		// tiếp tục thực hiện chuỗi filter
		filterChain.doFilter(request, response);
	}

}
