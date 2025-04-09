package com.vantu.shop_backend.exceptions;

public class AlreadyVerifiedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyVerifiedException(String message) {
		super(message);
	}

}
