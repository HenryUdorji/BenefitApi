package com.codemountain.benefitapi.payload.request;


import lombok.Data;

@Data
public class ForgotPasswordRequest {

    private String secretQuestion;
    private String secretAnswer;
    private String newPassword;
    private String confirmNewPassword;
}
