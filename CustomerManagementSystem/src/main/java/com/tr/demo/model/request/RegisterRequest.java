package com.tr.demo.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String userName;
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "invalid password!")
    private String password;
    @Email
    private String email;
    private String name;

}
