package com.project.book_store.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @NotNull(message = "Kitap ID boş olamaz!")
    private Long bookId;

    @NotNull(message = "Adet boş olamaz!")
    @Min(value = 1, message = "En az 1 adet ürün eklemelisiniz!")
    private Integer quantity;
}