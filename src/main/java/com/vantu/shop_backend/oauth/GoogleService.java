package com.vantu.shop_backend.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleService {
	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String googleClientId;

	public GoogleIdToken.Payload verifyIdToken(String idTokenString) {
		try {
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
					GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory())
					.setAudience(Collections.singletonList(googleClientId)).build();

			System.out.println(idTokenString + "\n" + googleClientId);
			
			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken != null) {
				return idToken.getPayload();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
