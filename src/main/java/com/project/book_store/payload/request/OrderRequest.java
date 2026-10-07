package com.project.book_store.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotEmpty(message = "Sipariş sepeti boş olamaz")
    private List<OrderItemRequest> orderItems;

    @NotBlank(message = "Teslim alacak kişinin adı zorunludur")
    private String recipientName;


    
    @NotBlank(message = "Teslimat telefon numarası zorunludur")
    @Pattern(regexp = "^[\\d\\s()+-]+$", message = "Telefon numarası sadece rakam ve ayraç (boşluk, +, -, parantez) içerebilir")
    @Size(min = 10, message = "Telefon numarası en az 10 rakam içermelidir")
    private String shippingPhone;


    @NotBlank(message = "Teslimat adresi zorunludur")
    @Size(min = 10, max = 500, message = "Lütfen açık adresinizi eksiksiz girin (en az 10 karakter)")
    private String shippingAddress;


    
    @NotBlank(message = "Kart üzerindeki isim zorunludur")
    private String cardHolderName;

    @NotBlank(message = "Kart numarası zorunludur")
    @Pattern(regexp = "^[0-9]{15,16}$", message = "Kart numarası 15-16 haneli olmalıdır")
    private String cardNumber;

    @NotBlank(message = "Son kullanma ayı zorunludur")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Ay 01-12 arasında olmalıdır")
    private String expireMonth;

    @NotBlank(message = "Son kullanma yılı zorunludur")
    @Pattern(regexp = "^[0-9]{4}$", message = "Yıl 4 haneli olmalıdır (örn. 2030)")
    private String expireYear;

    @NotBlank(message = "CVC zorunludur")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVC 3-4 haneli olmalıdır")
    private String cvc;
}