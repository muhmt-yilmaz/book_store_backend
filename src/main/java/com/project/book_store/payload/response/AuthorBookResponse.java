package com.project.book_store.payload.response;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorBookResponse {
    private Long id;
    private String title;
    private String isbn;
    private String coverImageUrl;
    private BigDecimal price;
    private Integer discountPercentage;
    private String categoryName;
}