package com.vantu.shop_backend.exceptions;

public class InvalidOtpException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidOtpException(String message) {
		super(message);
	}

}
