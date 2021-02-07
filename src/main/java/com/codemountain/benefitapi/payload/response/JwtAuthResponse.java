package com.codemountain.benefitapi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

	private String accessToken;
	private String tokenType;
	//private Long id;
	private String username;
	private String refreshToken;
	private Instant expiresAt;


}
