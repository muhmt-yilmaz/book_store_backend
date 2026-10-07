package com.project.book_store.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String coverImageUrl;
    private String authorName;
    private String categoryName;
    private Integer stockQuantity;
    
    private BigDecimal originalPrice;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subTotal;
}