package com.codemountain.benefitapi.payload.request;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordRequest {

    @NotBlank
    @Size(max = 100)
    private String oldPassword;
    @NotBlank
    @Size(max = 100)
    private String newPassword;
    @NotBlank
    @Size(max = 100)
    private String confirmNewPassword;
}
