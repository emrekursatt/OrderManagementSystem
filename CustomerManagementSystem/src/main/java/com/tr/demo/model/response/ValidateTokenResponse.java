package com.tr.demo.model.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName(value = "validated")
public class ValidateTokenResponse {

    private Boolean validated;
}
