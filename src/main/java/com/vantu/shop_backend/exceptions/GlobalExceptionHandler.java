package com.vantu.shop_backend.exceptions;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// lắng nghe các exception được ném ra trong bất kỳ controller nào.
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
		// dùng map để trả về Json
		Map<String, Object> body = new HashMap<>();
		body.put("error", "Forbidden");
		body.put("message", "You do not have permission to this action");

		return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		// dùng map để trả về Json
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String fieldName = error.getField(); // ví dụ: "password"
			String errorMessage = error.getDefaultMessage(); // ví dụ: "must not be blank"
			errors.put(fieldName, errorMessage);
		});

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	/*
	 * bắt bất kỳ lỗi gì (trường hợp bỏ sót chưa xử lý trycatch). ưu tiên hàm xử lý
	 * exception cụ thể hơn.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleAll(Exception e) {
		// dùng map để trả về Json
		Map<String, Object> body = new HashMap<>();
		body.put("error", "Internal Server Error");
		body.put("message", e.getMessage());
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
