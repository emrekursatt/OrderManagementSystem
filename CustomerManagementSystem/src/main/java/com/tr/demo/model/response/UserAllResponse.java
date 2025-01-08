package com.tr.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

@Data
@Builder
@AllArgsConstructor
@Repository
@NoArgsConstructor
public class UserAllResponse {

    private String userName;
    private String email;
    private boolean enabled;
    private String status;
}
