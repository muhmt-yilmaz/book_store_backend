package com.project.book_store.payload.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorResponse {

    private Long id;
    private String name;
    private String surname;
    private String biography;
    private String nationality;
    private Date birthDate;
    private String websiteUrl;


}