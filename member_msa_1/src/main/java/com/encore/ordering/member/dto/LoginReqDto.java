package com.encore.ordering.member.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class LoginReqDto {

    @NotEmpty(message = "Email must be entered.")
    @Email(message = "Email is invalid.")
    private String email;

    @NotEmpty(message = "Password must be entered.")
    @Size(min = 4, message = "Password should be more than 4 letters.")
    private String password;
}
