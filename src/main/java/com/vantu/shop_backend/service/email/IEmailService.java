package com.vantu.shop_backend.service.email;

public interface IEmailService {
	void sendEmail(String to, String subject, String body);
}
