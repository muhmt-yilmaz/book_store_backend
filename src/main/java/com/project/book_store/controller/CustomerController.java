package com.project.book_store.controller;

import com.project.book_store.payload.request.ProfileUpdateRequest;
import com.project.book_store.payload.response.CustomerResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.CustomerService;
import com.project.book_store.utils.Messages;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<CustomerResponse>> getProfile(Principal principal) {
        CustomerResponse profile = customerService.getProfile(principal.getName());

        ResponseMessage<CustomerResponse> response = ResponseMessage.<CustomerResponse>builder()
                .object(profile)
                .message(Messages.PROFILE_FETCHED_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    
    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<CustomerResponse>> updateProfile(
            Principal principal,
            @RequestBody @Valid ProfileUpdateRequest request) {

        CustomerResponse updated = customerService.updateProfile(principal.getName(), request);

        ResponseMessage<CustomerResponse> response = ResponseMessage.<CustomerResponse>builder()
                .object(updated)
                .message(Messages.PROFILE_UPDATED_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}
