package com.tr.demo.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
