package com.codemountain.benefitapi.payload.request;

import lombok.Data;


@Data
public class CreateUserRequest {

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String secretQuestion;

    private String secretAnswer;



}
