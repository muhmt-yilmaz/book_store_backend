package com.project.book_store.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    @NotNull(message = "Kitap ID zorunludur")
    private Long bookId;

    @NotNull(message = "Adet zorunludur")
    @Min(value = 1, message = "En az 1 adet sipariş edilmelidir")
    private Integer quantity;
}
