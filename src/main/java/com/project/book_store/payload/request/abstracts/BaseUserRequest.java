package com.project.book_store.payload.request.abstracts;

import com.project.book_store.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserRequest implements Serializable {

    @NotNull(message = "Please enter your name")
    @Size(min = 2,max = 16,message = "Your name should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your name must consist of the characters .")
    private String name;

    @NotNull(message = "Please enter your surname")
    @Size(min = 2,max = 16,message = "Your surname should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your surname must consist of the characters .")
    private String surname;

    @Email(message = "Please enter a valid email address")
    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$", message = "Please enter a valid email address (e.g. name@example.com)")
    @NotNull(message = "Please enter your email")
    @Size(min = 10, max = 50, message = "Your email should be between 10 and 50 chars")
    private String email;

    @NotNull(message = "Please enter your birthdate")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @Past
    private LocalDate birthDate;

    @Pattern(regexp = "^[1-9][0-9]{10}$", message = "Please enter a valid TC number")
    private String tc;


    @NotNull(message = "Please enter your password")
    @Size(min = 8,max = 60,message = "Your password should be at least 8 chars")
    private String password;

    @Size(min = 12, max = 12, message = "Phone number must be exactly 12 digits, including country code (e.g., 905554443322)")
    @Pattern(regexp = "^\\d{12}$", message = "Please enter only digits (12 characters total)")
    private String phoneNumber;

    @NotNull(message = "Please enter your gender")
    private Gender gender;
}