package com.vantu.shop_backend.security.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.vantu.shop_backend.security.jwt.JwtAuthEntryPoint;
import com.vantu.shop_backend.security.jwt.JwtFilter;
import com.vantu.shop_backend.security.user.ShopUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ShopConfig {

	private final JwtAuthEntryPoint jwtAuthEntryPoint;
	private final ShopUserDetailsService shopUserDetailsService;
	private final JwtFilter jwtFilter;

	/*
	 * có thể đổi tên phương thức bất kỳ vì Spring chỉ quan tâm phương thức này đã
	 * đc đk bởi bean và trả về 1 ModelMapper
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * AuthenticationManager này được cấu hình dựa trên các cấu hình bảo mật đã khai
	 * báo trước đó (ví dụ: UserDetailsService, PasswordEncoder, v.v.), có
	 * thể @Autowired AuthenticationManager ở bất cứ đâu cần xác thực người dùng, ví
	 * dụ: controller xử lý đăng nhập, custom filter xác thực JWT.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/*
	 * thay vì để cấu hình mặc định thì tự cấu hình lại AuthenticationProvider, tạo
	 * và trả về một đối tượng DaoAuthenticationProvider chứa thông tin user đã đc
	 * xác thực
	 */
	@Bean
	public AuthenticationProvider authProvider() {
		// là lớp triển khai của AuthenticationProvider dùng xác thực user
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		/*
		 * DaoAuthenticationProvider sẽ sử dụng userDetailsService để gọi phương thức
		 * loadUserByUsername(String username) khi người dùng đăng nhập. Dùng
		 * DaoAuthenticationProvider để làm AuthenticationProvider chính cho
		 * AuthenticationManager
		 */
		provider.setUserDetailsService(this.shopUserDetailsService);
		provider.setPasswordEncoder(this.passwordEncoder());

		return provider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		/*
		 * Tắt CSRF protection, tách bach-front end riêng nên ko có web form để tấn công
		 * dạng này
		 */
		http.csrf(AbstractHttpConfigurer::disable)
				// Cấu hình EntryPoint cho lỗi xác thực
				.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
				// Cấu hình session không lưu trữ (Stateless)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// Các URL bảo mật yêu cầu xác thực và phân quyền theo vai trò
				.authorizeHttpRequests(auth -> auth

						// BrandController
						.requestMatchers("/api/v1/branches/all").permitAll().requestMatchers("/api/v1/branches/**")
						.hasAnyAuthority("ADMIN")

						// CartController, CartItemControlle
						.requestMatchers("/api/v1/carts/**", "/api/v1/cart-items/**").hasAnyAuthority("USER")

						// CategoryController
						.requestMatchers("/api/v1/categories/all", "/api/v1/categories/category/id/**",
								"/api/v1/categories/category/name/**")
						.permitAll().requestMatchers("/api/v1/categories/**").hasAnyAuthority("ADMIN")

						// ImageController
						.requestMatchers("/api/v1/images/image/download/**").permitAll()
						// .requestMatchers("/api/v1/images/**").hasAnyAuthority("ADMIN", "USER")
						.requestMatchers("/api/v1/images/**").permitAll()

						// OrderController
						.requestMatchers("/api/v1/orders/**").hasAnyAuthority("USER")

						// ProductController
						.requestMatchers("/api/v1/products/all", "/api/v1/products/product/id/**",
								"/api/v1/products/product/name/**", "/api/v1/products/brand/name/**",
								"/api/v1/products/category/name/**", "/api/v1/products/by/productname-and-brandname",
								"/api/v1/products/by/categoryname-and-brandname", "/api/v1/products/count")
						.permitAll().requestMatchers("/api/v1/products/**").hasAuthority("ADMIN")

						// UserController
						.requestMatchers("/api/v1/users/user/favorite/**").hasAnyAuthority("USER")
						.requestMatchers("/api/v1/users/user/id/**").hasAnyAuthority("USER")
						.requestMatchers("/api/v1/users/**").hasAnyAuthority("ADMIN")

						// AuthController
						.requestMatchers("/api/v1/auth/**").permitAll()

						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v2/api-docs", "/swagger-resources/**",
								"/swagger-ui.html", "/webjars/**")
						.permitAll());

		// Cung cấp AuthenticationProvider (như DaoAuthenticationProvider)
		http.authenticationProvider(authProvider());
		/*
		 * Thêm JWT filter vào trước filter xác thực mặc định của Spring Security, đảm
		 * bảo token được kiểm tra trước khi tiến hành xác thực người dùng
		 */
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		// Trả về cấu hình đã setup
		return http.build();
	}

}
