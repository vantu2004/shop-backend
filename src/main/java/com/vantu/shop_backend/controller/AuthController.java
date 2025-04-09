package com.vantu.shop_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.shop_backend.dto.UserDto;
import com.vantu.shop_backend.exceptions.AlreadyExistsException;
import com.vantu.shop_backend.exceptions.InvalidOtpException;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.request.CreateUserRequest;
import com.vantu.shop_backend.request.LoginRequest;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.response.JwtRepsonse;
import com.vantu.shop_backend.security.jwt.JwtService;
import com.vantu.shop_backend.security.user.ShopUserDetails;
import com.vantu.shop_backend.service.user.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/auth")
public class AuthController {
	/*
	 * được khai báo bên ShopConfig, vì đã config lại nên buộc phải đk bean lại nếu
	 * ko sẽ dùng authenticationManager mặc định
	 */
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final IUserService iUserService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			String token = jwtService.generateToken(authentication);
			// authentication đại diện cho thông tin xác thực của người dùng
			// getPrincipal() trả về đối tượng đại diện người dùng đã xác thực
			ShopUserDetails shopUserDetails = (ShopUserDetails) authentication.getPrincipal();

			JwtRepsonse jwtRepsonse = new JwtRepsonse(shopUserDetails.getId(), token);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", jwtRepsonse));
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
		try {
			User user = this.iUserService.createUser(request);
			UserDto userDto = this.iUserService.convertUserEntityToUserDto(user);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", userDto));
		} catch (AlreadyExistsException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PostMapping("/verify")
	public ResponseEntity<ApiResponse> verifyUser(@RequestParam String email, @RequestParam String otp) {
		try {
			iUserService.verify(email, otp);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (ResourceNotFoundException | UsernameNotFoundException | InvalidOtpException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PostMapping("/forgot")
	public ResponseEntity<ApiResponse> sendResetPasswordOtp(@RequestParam String email) {
		try {
			iUserService.resendOtp(email);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse> resetPassword(@RequestParam String email, @RequestParam String newPassword,
			@RequestParam String otp) {
		try {
			this.iUserService.resetPassword(email, newPassword, otp);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (UsernameNotFoundException | InvalidOtpException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
		}
	}

	/*
	 * trường hợp user sau khi đăng ký xong ko xác thực otp thì cho phép gửi lại otp
	 * và chuyển đến link verify xác thực lại
	 */
	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse> resendOtp(@RequestParam String email) {
		try {
			this.iUserService.resendOtp(email);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
		}
	}
}
