package com.project.book_store.payload.response;

import com.project.book_store.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Integer quantity;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;

    private String recipientName;
    private String shippingPhone;
    private String shippingAddress;
    private String paymentId;

    private Long customerId;
    private String customerName;
    private String customerEmail;

    private List<OrderItemResponse> orderItems;
}
