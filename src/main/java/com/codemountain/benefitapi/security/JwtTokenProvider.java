package com.codemountain.benefitapi.security;

import com.codemountain.benefitapi.exception.ApiException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

import static java.util.Date.from;

@Component
@Slf4j
public class JwtTokenProvider {

	@Value(value = "${app.jwtSecret}")
	private String jwtSecret;

	@Value(value = "${app.jwtExpirationInMs}")
	private Long jwtExpirationInMs;

	public String generateToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder()
				.setSubject(userPrincipal.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}


	//For refreshToken
	public String generateTokenWithUsername(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(from(Instant.now()))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.setExpiration(java.sql.Date.from(Instant.now().plusMillis(jwtExpirationInMs)))
				.compact();
	}



	public String getUsernameFromJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(token)
				.getBody();

		return claims.getSubject();
	}



	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature");
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty");
			throw new ApiException(HttpStatus.UNAUTHORIZED, "JWT claims string is empty");
		}
		//return false;
	}

	public Long getJwtExpirationInMs() {
		return jwtExpirationInMs;
	}
}
