package com.project.book_store.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @NotBlank(message = "Kitap adı zorunludur")
    private String title;

    private String isbn;

    @NotBlank(message = "SKU kodu zorunludur")
    private String sku;

    @NotNull(message = "Stok adedi boş olamaz!")
    @Min(value = 0, message = "Stok adedi 0'dan küçük olamaz!")
    private Integer stockQuantity;


    @Min(value = 0, message = "İndirim yüzdesi 0'dan küçük olamaz!")
    @Max(value = 100, message = "İndirim yüzdesi 100'den büyük olamaz!")
    private Integer discountPercentage;

    private String description;
    private String coverImageUrl;
    private Integer pageCount;
    private Integer publicationYear;

    @NotNull(message = "Fiyat zorunludur")
    private BigDecimal price;

    
    @NotNull(message = "Yazar seçilmelidir")
    private Long authorId;

    @NotNull(message = "Yayınevi seçilmelidir")
    private Long publisherId;

    @NotNull(message = "Kategori seçilmelidir")
    private Long categoryId;
}
