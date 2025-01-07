package com.tr.demo.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateTokenResponse {

    private Boolean validated;
}
