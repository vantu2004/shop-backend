package com.vantu.shop_backend.service.user;

import com.vantu.shop_backend.dto.UserDto;
import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.request.CreateUserRequest;
import com.vantu.shop_backend.request.UserUpdateRequest;

public interface IUserService {
	User getUserById(Long id);

	User createUser(CreateUserRequest request);

	User updateUser(UserUpdateRequest request, Long userId);

	void deleteUser(Long userId);

	UserDto convertUserEntityToUserDto(User user);

	User getAuthenticatedUser();

	void verify(String email, String otp);

	void resetPassword(String email, String newPassword, String otp);

	void resendOtp(String email);

	UserDto findOrCreateUser(String email, String name, String pictureUrl);
}
