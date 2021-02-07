package com.codemountain.benefitapi.service;


import com.codemountain.benefitapi.entities.User;
import com.codemountain.benefitapi.exception.ApiException;
import com.codemountain.benefitapi.payload.request.*;
import com.codemountain.benefitapi.payload.response.ApiResponse;
import com.codemountain.benefitapi.payload.response.SecretQuestionResponse;
import com.codemountain.benefitapi.payload.response.JwtAuthResponse;
import com.codemountain.benefitapi.repository.UserRepository;
import com.codemountain.benefitapi.security.JwtTokenProvider;
import com.codemountain.benefitapi.security.UserPrincipal;
import com.codemountain.benefitapi.utils.Constants;
import com.codemountain.benefitapi.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;



    @Transactional
    public void createAdmin() {

        User admin = new User();
        admin.setUsername("Admin");
        admin.setPassword(passwordEncoder.encode("root"));
        admin.setRole(Constants.ADMIN);
        admin.setCreatedAt(Utils.getDate());

        userRepository.save(admin);
    }


    @Transactional
    public ApiResponse createUser(CreateUserRequest createUserRequest) {
        String firstName      = createUserRequest.getFirstName();
        String lastName       = createUserRequest.getLastName();
        String password       = createUserRequest.getPassword();
        String username       = createUserRequest.getUsername();
        String secretAnswer   = createUserRequest.getSecretAnswer();
        String secretQuestion = createUserRequest.getSecretQuestion();

        if (secretAnswer == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Secret answer cannot be empty");
        }
        if (Boolean.TRUE.equals(userRepository.existsByUsername(username))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setSecretQuestion(secretQuestion);
        user.setSecretAnswer(secretAnswer);
        user.setReferralCode(username);
        user.setRole(Constants.USER);
        user.setCreatedAt(Utils.getDate());

        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "User Account created successfully");
    }


    @Transactional
    public JwtAuthResponse login(LoginRequest loginRequest) {
        String password = loginRequest.getPassword();
        String username = loginRequest.getUsername();

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken      = jwtTokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .username(principal.getUsername())
                .tokenType("Bearer")
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getJwtExpirationInMs()))
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .build();

    }


    @Transactional
    public JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String username     = refreshTokenRequest.getUsername();

        refreshTokenService.validateRefreshToken(refreshToken);
        String token = jwtTokenProvider.generateTokenWithUsername(username);

        refreshTokenService.deleteRefreshToken(refreshToken);
        return JwtAuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getJwtExpirationInMs()))
                .username(username)
                .build();
    }


    @Transactional
    public ApiResponse logout(String refreshToken) {
        return refreshTokenService.deleteRefreshToken(refreshToken);
    }


    /*************************************
     * Only authenticated user && Admin can call this endpoint
     * */
    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest resetPasswordRequest, UserPrincipal currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow(() ->
                new ApiException(HttpStatus.UNAUTHORIZED, "You don't have permission to reset this password"));

        String oldPassword  = resetPasswordRequest.getOldPassword();
        String passwordInDb = user.getPassword();

        if (passwordEncoder.matches(oldPassword, passwordInDb)) {
            String newPassword        = resetPasswordRequest.getNewPassword();
            String confirmNewPassword = resetPasswordRequest.getConfirmNewPassword();

            if (newPassword.equals(confirmNewPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return new ApiResponse(Boolean.TRUE, "Password reset successful");
            }else {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Your new password and confirm password need to match " +
                        "in other to proceed");
            }
        }else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Old Password entered does not match the currently " +
                    "available password in the Database");
        }
     }


    /*************************************
     * User has not been authenticated and has forgotten
     * their password the earlier set security question
     * would be used to retrieve their password. The User
     * would be prompted to reset their password.
     */
    @Transactional(readOnly = true)
    public SecretQuestionResponse secretQuestion(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "Username not found"));

        SecretQuestionResponse secretQuestionResponse = new SecretQuestionResponse();
        secretQuestionResponse.setSecretQuestion(user.getSecretQuestion());
        return secretQuestionResponse;
    }


    @Transactional
    public ApiResponse forgotPassword(String username, ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "Username not found"));

        String secretAnswer       = forgotPasswordRequest.getSecretAnswer();
        String secretQuestion     = forgotPasswordRequest.getSecretQuestion();
        String newPassword        = forgotPasswordRequest.getNewPassword();
        String confirmNewPassword = forgotPasswordRequest.getConfirmNewPassword();

        if (user.getSecretQuestion().equals(secretQuestion) && user.getSecretAnswer().equals(secretAnswer)) {

            if (newPassword.equals(confirmNewPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return new ApiResponse(Boolean.TRUE, "Password reset successful");
            }else {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Your new password and confirm password need to match " +
                        "in other to proceed");
            }
        }else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Your Secret Answer is wrong!!");
        }
    }
}
