package com.codemountain.benefitapi.service;


import com.codemountain.benefitapi.entities.RefreshToken;
import com.codemountain.benefitapi.exception.ApiException;
import com.codemountain.benefitapi.payload.response.ApiResponse;
import com.codemountain.benefitapi.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    /**********************************
     * Generate random UUID to process
     * token refresh
     * @return
     */
    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }


    void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh Token"));
    }


    public ApiResponse deleteRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "Token not found"));

        /*******************************************
        * This token would be deleted on two different occasion
        * 1. when the user refreshes the auth token
        * 2. when the user logs out of their account
        *
        * For this response we would show logout message
        * */
        if (refreshToken.getToken().equals(token)) {
            refreshTokenRepository.deleteByToken(token);
            return new ApiResponse(Boolean.TRUE, "Log out successful");
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot delete an invalid token");
    }
}
