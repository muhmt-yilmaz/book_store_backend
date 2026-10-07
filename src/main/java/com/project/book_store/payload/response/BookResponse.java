package com.project.book_store.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private String sku;

    private Integer stockQuantity;
    private Integer discountPercentage;

    private String description;
    private String coverImageUrl;
    private Integer pageCount;
    private Integer publicationYear;
    private BigDecimal price;

    private Long authorId;
    private String authorName;

    private Long publisherId;
    private String publisherName;

    private Long categoryId;
    private String categoryName;
}