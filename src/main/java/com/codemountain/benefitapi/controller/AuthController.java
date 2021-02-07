package com.codemountain.benefitapi.controller;


import com.codemountain.benefitapi.payload.request.*;
import com.codemountain.benefitapi.payload.response.ApiResponse;
import com.codemountain.benefitapi.payload.response.SecretQuestionResponse;
import com.codemountain.benefitapi.payload.response.JwtAuthResponse;
import com.codemountain.benefitapi.security.CurrentUser;
import com.codemountain.benefitapi.security.UserPrincipal;
import com.codemountain.benefitapi.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@EnableTransactionManagement
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;


    //TODO -> find a way to create admin account once and for all without trying the endpoint again
    //@PostConstruct
    @PostMapping("/create/admin")
    public void createAdmin() {
        authService.createAdmin();
    }


    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        ApiResponse apiResponse = authService.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtAuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtAuthResponse refreshResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body(refreshResponse);
    }


    @DeleteMapping("/logout/{refreshToken}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse> logout(@PathVariable String refreshToken) {
        ApiResponse apiResponse = authService.logout(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    @PatchMapping("/reset-password")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest,
                                                     @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = authService.resetPassword(resetPasswordRequest, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    @GetMapping("/secret-question/{username}")
    public ResponseEntity<SecretQuestionResponse> secretQuestion(@PathVariable String username) {
        SecretQuestionResponse secretQuestionResponse = authService.secretQuestion(username);
        return ResponseEntity.status(HttpStatus.OK).body(secretQuestionResponse);
    }


    @PostMapping("/forgot-password/{username}")
    public ResponseEntity<ApiResponse> forgotPassword(@PathVariable String username,
                                                      @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        ApiResponse apiResponse = authService.forgotPassword(username, forgotPasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
