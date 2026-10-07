package com.project.book_store.payload.response;

import lombok.*;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDetailResponse {
    private Long id;
    private String name;
    private String surname;
    private String biography;
    private String nationality;
    private Date birthDate;
    private String websiteUrl;
    private List<AuthorBookResponse> books;
}
