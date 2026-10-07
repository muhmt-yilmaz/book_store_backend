package com.project.book_store.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyRequest {

    @NotBlank(message = "E-posta alanı boş bırakılamaz")
    private String email;

    @NotBlank(message = "Doğrulama kodu boş bırakılamaz")
    private String code;
}