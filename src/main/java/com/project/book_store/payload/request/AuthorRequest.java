package com.project.book_store.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorRequest {

    @NotBlank(message = "Yazar adı boş bırakılamaz.")
    @Size(min = 2, max = 50, message = "Yazar adı 2 ile 50 karakter arasında olmalıdır.")
    private String name;

    @NotBlank(message = "Yazar soyadı boş bırakılamaz.")
    @Size(min = 2, max = 50, message = "Yazar soyadı 2 ile 50 karakter arasında olmalıdır.")
    private String surname;

    @Size(max = 1000, message = "Biyografi en fazla 1000 karakter olabilir.")
    private String biography;

    private String nationality;

    @Past(message = "Doğum tarihi geçmiş bir tarih olmalıdır.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    @URL(message = "Geçersiz web sitesi adresi.")
    private String websiteUrl;
}
