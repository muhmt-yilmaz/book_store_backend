package com.project.book_store.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email boş bırakılamaz")
    @Email(message = "Lütfen geçerli bir email giriniz")
    private String email;

    @NotBlank(message = "Password must not be empty")
    private String password;
}