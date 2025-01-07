package com.tr.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(hidden = true)
public class ChangePasswordRequest {

    private String currentPassword;
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "invalid password!")
    private String newPassword;
}
