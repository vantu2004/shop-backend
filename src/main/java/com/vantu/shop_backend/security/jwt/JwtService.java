package com.vantu.shop_backend.security.jwt;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.security.user.ShopUserDetails;
import com.vantu.shop_backend.security.user.ShopUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final String secretKeyBase64;
	private final ShopUserDetailsService shopUserDetailsService;

	public JwtService(ShopUserDetailsService shopUserDetailsService) {
		this.secretKeyBase64 = generateSecretKey();
		this.shopUserDetailsService = shopUserDetailsService;
	}

	// Phương thức này tạo khóa bí mật ngẫu nhiên và mã hóa nó bằng Base64
	private String generateSecretKey() {
		try {
			// Khởi tạo KeyGenerator với thuật toán HMACSHA256
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			// Tạo khóa bí mật
			SecretKey secretKey = keyGen.generateKey();

			// Mã hóa khóa bí mật thành chuỗi Base64, bản chất SecretKey là mảng byte[]
			/*
			 * (mảng byte là tập các byte, mỗi byte là mỗi nhóm 8 bit đc trích từ chuỗi
			 * binary của dữ liệu đã đc convert trước đó) nên ko phải lúc nào cũng dễ dàng
			 * truyền đi đc -> convert sang base64 (base64 là chuỗi văn bản được convert từ
			 * mảng byte) để dễ dàng truyền đi
			 */
			return Base64.getEncoder().encodeToString(secretKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error generating secretKey", e);
		}
	}

	// Phương thức này sẽ tạo JWT token cho tên người dùng (userName)
	public String generateToken(String email) {

		/*
		 * kiểu trả về của loadByUserName là UserDetails nhưng vẫn có thể ép về
		 * ShopUserDetails theo tính đa hình
		 */
		ShopUserDetails shopUserDetails = (ShopUserDetails) this.shopUserDetailsService.loadUserByUsername(email);

		List<String> roles = shopUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		/*
		 * chứa thông tin bổ sung khi tạo token, thay vì thêm từng dòng thủ công thì
		 * dùng map
		 */
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", shopUserDetails.getId());
		claims.put("roles", roles);

		return Jwts.builder().subject(shopUserDetails.getUsername())
				// Thêm các thông tin bổ sung vào claims (ở đây là một Map rỗng)
				.claim("claims", claims)
				// Thời gian phát hành token
				.issuedAt(new Date())
				// Thời gian hết hạn token (3 phút)
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3))
				// Sử dụng khóa bí mật để ký token
				.signWith(getSecretKey())
				// Chuyển token thành chuỗi
				.compact();
	}

	// Phương thức lấy secretKey từ Base64
	private Key getSecretKey() {
		// convert từ chuỗi Base64 sạng lại mảng byte
		byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);

		// Chuyển đổi thành Key với thuật toán HMAC-SHA256
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// Phương thức này trích xuất tên người dùng từ token JWT
	public String extractUserName(String token) {
		// Trích xuất chủ đề (subject) là tên người dùng
		return extractClaim(token, Claims::getSubject);
	}

	// Phương thức này trích xuất thông tin từ các claims từ token JWT
	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		// Trích xuất tất cả các claims từ token
		final Claims claims = extractAllClaims(token);
		// Sử dụng claimsResolver để lấy thông tin cụ thể từ claims
		return claimsResolver.apply(claims);
	}

	// Phương thức này trích xuất tất cả các claims từ token JWT
	private Claims extractAllClaims(String token) {
		// Đối tượng này sẽ phân tích và xác minh chữ ký của JWT.
		// Đặt khóa bí mật để kiểm tra chữ ký của token
		JwtParser parser = Jwts.parser().verifyWith((SecretKey) getSecretKey()).build();

		// Phân tích token thành một đối tượng Jws chứa các Claims
		// Token đã ký được phân tích và chuyển thành Jws
		Jws<Claims> jws = parser.parseSignedClaims(token);

		// Trả về phần payload (nội dung chính) của token, chứa các claims
		// Payload là nơi chứa thông tin (claims) của token
		return jws.getPayload();
	}

	// Phương thức này kiểm tra tính hợp lệ của token
	public boolean validateToken(String token, UserDetails userDetails) {
		final String userName = extractUserName(token);

		// Kiểm tra xem tên người dùng có trùng khớp và token có hết hạn không
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// Phương thức kiểm tra token đã hết hạn chưa
	private boolean isTokenExpired(String token) {
		// Nếu thời gian hết hạn trước thời điểm hiện tại thì token hết hạn
		return extractExpiration(token).before(new Date());
	}

	// Phương thức này trích xuất thời gian hết hạn từ token JWT
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
}
