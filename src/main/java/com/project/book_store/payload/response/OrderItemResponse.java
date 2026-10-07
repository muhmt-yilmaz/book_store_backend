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
public class OrderItemResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String coverImageUrl;
    private String authorName;
    private Integer quantity;
    private BigDecimal price;
}
