package app.bola.smartnotesai.security.provider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtAuthenticationProvider {
	
	@Value("${app.jwt.access-secret}")
	private String accessTokenSecret;
	
	@Value("${app.jwt.refresh-secret}")
	private String refreshTokenSecret;
	
	@Value("${app.jwt.access-expiration}")
	private String accessExpiration;
	
	@Value("${app.jwt.refresh-expiration}")
	private String refreshExpiration;
	
	public String generateRefreshToken(UserDetails user) {
		return generateToken(
			user,
			Map.of("email", user.getUsername(), "roles", user.getAuthorities()),
			refreshTokenSecret,
			Date.from(Instant.now().plusMillis(Long.parseLong(refreshExpiration)))
		);
	}
	
	public String generateAccessToken(UserDetails userDetails) {
		return generateToken(
			userDetails,
			Map.of("username", userDetails.getUsername(), "roles", userDetails.getAuthorities()),
			accessTokenSecret,
			Date.from(Instant.now().plusMillis(Long.parseLong(accessExpiration)))
		);
	}
	
	public String generateToken(final UserDetails userDetails, Map<String, Object> claims, String secret, Date expiryDate) {
		SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
		return Jwts.builder()
			       .subject(userDetails.getUsername())
			       .claims(claims)
			       .issuedAt(Date.from(Instant.now()))
			       .expiration(expiryDate)
			       .signWith(secretKey, Jwts.SIG.HS512)
			       .compact();
				
	}
	
	public String extractUsername(final String token, boolean isRefresh) {
		String secret = isRefresh ? refreshTokenSecret : accessTokenSecret;
		SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
		return Jwts.parser()
			       .verifyWith(secretKey)
			       .build()
			       .parseSignedClaims(token)
			       .getPayload()
			       .getSubject();
	}
	
	private boolean isExpiredToken(String token) {
		SecretKey secretKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes());
		Date expiration = Jwts.parser()
				             .verifyWith(secretKey)
				             .build()
				             .parseSignedClaims(token)
				             .getPayload()
				             .getExpiration();
		return expiration.before(Date.from(Instant.now()));
	}
	
	public boolean validateToken(final String token, final UserDetails userDetails, boolean isRefresh) {
		final String username = extractUsername(token, isRefresh);
		return username.equals(userDetails.getUsername()) && !isExpiredToken(token);
	}
}
