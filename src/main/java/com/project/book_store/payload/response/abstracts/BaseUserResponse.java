package com.project.book_store.payload.response.abstracts;

import com.project.book_store.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserResponse {

    private Long userId;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private String tc;
    private String phoneNumber;
    private Gender gender;
    private boolean isEnabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
