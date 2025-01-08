package com.tr.demo.controller;

import com.tr.demo.model.request.ChangePasswordRequest;
import com.tr.demo.model.request.RegisterRequest;
import com.tr.demo.model.response.BaseResponse;
import com.tr.demo.model.response.UserAllResponse;
import com.tr.demo.security.CustomerPrincipal;
import com.tr.demo.service.CustomerService;
import com.tr.demo.service.PasswordService;
import com.tr.demo.service.RegistrationService;
import com.tr.demo.util.ResponseEntityWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.tr.demo.advice.constants.RestAPIConstants.API_CUSTOMER;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_CUSTOMER)
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{username}")
    public ResponseEntity<UserAllResponse> getUserByUserName(@PathVariable String username) {
        return ResponseEntity.ok(customerService.getUserByUserName(username));
    }


    private final RegistrationService registrationService;

    @PostMapping("/customer-register")
    public ResponseEntityWrapper<String> registerUser(@AuthenticationPrincipal final CustomerPrincipal userPrincipal,
                                                      @RequestBody final RegisterRequest registerRequest) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        registrationService.registerUser(registerRequest, userPrincipal);
        baseResponse.setData(registrationService.registerUser(registerRequest, userPrincipal));
        baseResponse.setMessage("Customer registered successfully");
        return new ResponseEntityWrapper<>(baseResponse);
    }


    private final PasswordService passwordService;

    @PutMapping("/change-password")
    public ResponseEntity<HttpStatus> changePassword(
            @AuthenticationPrincipal final CustomerPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        passwordService.changePassword(userPrincipal, changePasswordRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
