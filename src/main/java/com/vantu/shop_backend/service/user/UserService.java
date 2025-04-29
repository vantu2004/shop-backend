package com.vantu.shop_backend.service.user;

import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.dto.UserDto;
import com.vantu.shop_backend.exceptions.AlreadyExistsException;
import com.vantu.shop_backend.exceptions.AlreadyVerifiedException;
import com.vantu.shop_backend.exceptions.InvalidOtpException;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.User;
import com.vantu.shop_backend.repository.RoleRepository;
import com.vantu.shop_backend.repository.UserRepository;
import com.vantu.shop_backend.request.CreateUserRequest;
import com.vantu.shop_backend.request.UserUpdateRequest;
import com.vantu.shop_backend.service.email.IEmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final IEmailService iEmailService;

	@Override
	public User getUserById(Long userId) {
		// TODO Auto-generated method stub
		return this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found!"));
	}

	@Override
	public User createUser(CreateUserRequest request) {

		// TODO Auto-generated method stub
		return Optional.of(request).filter(user -> !this.userRepository.existsByEmail(request.getEmail())).map(req -> {
			User user = new User();
			user.setFirstName(req.getFirstName());
			user.setLastName(req.getLastName());
			user.setEmail(req.getEmail());
			user.setPassword(passwordEncoder.encode(req.getPassword()));
			user.setOtp(this.generateOTP());

			// mặc định set role USER
			user.getRoles().add(this.roleRepository.findByName("USER"));

			sendVerificationEmail(user.getEmail(), user.getOtp());

			return this.userRepository.save(user);
		}).orElseThrow(() -> new AlreadyExistsException(request.getEmail() + " Already Exist!"));
	}

	@Override
	public User updateUser(UserUpdateRequest request, Long userId) {
		// TODO Auto-generated method stub
		return this.userRepository.findById(userId).map(existingUser -> {
			existingUser.setFirstName(request.getFirstName());
			existingUser.setLastName(request.getLastName());

			return this.userRepository.save(existingUser);
		}).orElseThrow(() -> new ResourceNotFoundException("User Not Found!"));
	}

	@Override
	public void deleteUser(Long userId) {
		// TODO Auto-generated method stub
		this.userRepository.findById(userId).ifPresentOrElse(this.userRepository::delete, () -> {
			throw new ResourceNotFoundException("User Not Found!");
		});
	}

	@Override
	public UserDto convertUserEntityToUserDto(User user) {
		return this.modelMapper.map(user, UserDto.class);
	}

	// dùng để lấy thông tin user đã đăng nhập khi addProductToCart
	@Override
	public User getAuthenticatedUser() {
		// TODO Auto-generated method stub
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		return this.userRepository.findByEmail(email);
	}

	private String generateOTP() {
		Random random = new Random();
		int otpValue = 100000 + random.nextInt(900000);
		return String.valueOf(otpValue);
	}

	private void sendVerificationEmail(String email, String otp) {
		String subject = "Email verification";
		String body = "Your verification otp is: " + otp;
		this.iEmailService.sendEmail(email, subject, body);
	}

	@Override
	public void verify(String email, String otp) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new ResourceNotFoundException("User not found with email: " + email);
		}

//		if (user.isVerified()) {
//			throw new AlreadyVerifiedException("User is already verified.");
//		}

		if (!otp.equals(user.getOtp())) {
			throw new InvalidOtpException("Invalid OTP.");
		}

		user.setVerified(true);
		user.setOtp(null); // clear OTP after verification
		userRepository.save(user);
	}

	@Override
	public void resendOtp(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + email);
		}

		String otp = generateOTP();
		user.setOtp(otp);
		userRepository.save(user);

		this.sendVerificationEmail(email, otp);
	}

	@Override
	public void resetPassword(String email, String newPassword) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + email);
		}

		String encodedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(encodedPassword);
		// clear OTP after use
		user.setOtp(null);
		userRepository.save(user);
	}

	@Override
	public UserDto findOrCreateUser(String email, String name, String pictureUrl) {

		User user = this.userRepository.findByEmail(email);
		if (user != null) {
			return this.convertUserEntityToUserDto(user);
		}

		user = new User();
		user.setEmail(email);
		this.setName(name, user);
		user.setVerified(true);

		// mặc định set role USER
		user.getRoles().add(this.roleRepository.findByName("USER"));

		User savedUser = this.userRepository.save(user);

		return this.convertUserEntityToUserDto(savedUser);
	}

	private void setName(String name, User user) {
		// TODO Auto-generated method stub
		String[] names = name.split(" ");
		if (names.length < 2) {
			user.setFirstName(name);
		} else {
			String firstName = names[0];
			String lastName = name.replaceFirst(firstName + " ", "");

			user.setFirstName(firstName);
			user.setLastName(lastName);
		}
	}

}
