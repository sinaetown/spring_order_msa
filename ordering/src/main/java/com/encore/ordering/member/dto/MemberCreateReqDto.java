package com.encore.ordering.member.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class MemberCreateReqDto {
    @NotEmpty(message = "Name must be set.")
    private String name;

    @NotEmpty(message = "Email must be set.")
    @Email(message = "Email is invalid.")
    private String email;

    @NotEmpty(message = "Password must be set.")
    @Size(min = 4, message = "Password should be more than 4 letters.")
    private String password;

    private String city;
    private String street;
    private String zipcode;
}
