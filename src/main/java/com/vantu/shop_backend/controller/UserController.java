package com.vantu.shop_backend.controller;

import com.vantu.shop_backend.request.UserPasswordUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.shop_backend.dto.UserDto;
import com.vantu.shop_backend.exceptions.AlreadyExistsException;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.request.CreateUserRequest;
import com.vantu.shop_backend.request.UserUpdateRequest;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.service.user.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

	private final IUserService iUserService;

	@GetMapping("/user/id/{userId}")
	public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
		try {
			User user = this.iUserService.getUserById(userId);
			UserDto userDto = this.iUserService.convertUserEntityToUserDto(user);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", userDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PostMapping("/add")
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

	@PutMapping("/user/{userId}/update")
	public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable Long userId) {
		try {
			User user = this.iUserService.updateUser(request, userId);
			UserDto userDto = this.iUserService.convertUserEntityToUserDto(user);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", userDto));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PutMapping("/user/{userId}/update-password")
	public ResponseEntity<ApiResponse> updatePassword(@RequestBody UserPasswordUpdateRequest request, @PathVariable Long userId) {
		try {
			this.iUserService.updatePassword(request, userId);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Password updated successfully!", null));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PutMapping("/user/favorite/{userId}/{productId}")
	public ResponseEntity<ApiResponse> saveFavoriteProduct(@PathVariable Long userId, @PathVariable Long productId) {
		try {
			User updatedUser = this.iUserService.handleSaveFavoriteProduct(userId, productId);
			UserDto userDto = this.iUserService.convertUserEntityToUserDto(updatedUser);

			return ResponseEntity.status(HttpStatus.OK)
					.body(new ApiResponse("Toggled favorite successfully!", userDto));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@DeleteMapping("/user/{userId}/delete")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
		try {
			this.iUserService.deleteUser(userId);

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", null));
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}
}
