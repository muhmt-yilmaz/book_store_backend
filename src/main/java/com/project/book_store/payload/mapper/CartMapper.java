package com.project.book_store.payload.mapper;

import com.project.book_store.entity.concretes.Cart;
import com.project.book_store.entity.concretes.CartItem;
import com.project.book_store.payload.response.CartItemResponse;
import com.project.book_store.payload.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "items", source = "cartItems", qualifiedByName = "mapItems")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cart.getCartItems()))")
    @Mapping(target = "cartId", source = "id")
    CartResponse toCartResponse(Cart cart);

    @Named("mapItems")
    default List<CartItemResponse> mapItems(List<CartItem> cartItems) {
        if (cartItems == null) return null;
        return cartItems.stream().map(this::toCartItemResponse).collect(Collectors.toList());
    }

    
    default CartItemResponse toCartItemResponse(CartItem item) {
        if (item == null) return null;

        BigDecimal originalPrice = item.getBook().getPrice();
        Integer discount = item.getBook().getDiscountPercentage();

        BigDecimal unitPrice = originalPrice;

        if (discount != null && discount > 0) {
            
            BigDecimal discountFactor = BigDecimal.valueOf(100 - discount)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            
            unitPrice = originalPrice.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
        }

        
        BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())).setScale(2, RoundingMode.HALF_UP);

        return CartItemResponse.builder()
                .id(item.getId())
                .bookId(item.getBook().getId())
                .bookTitle(item.getBook().getTitle())
                .coverImageUrl(item.getBook().getCoverImageUrl())
                .authorName(item.getBook().getAuthor() != null ? item.getBook().getAuthor().getName() : null)
                .categoryName(item.getBook().getCategory() != null ? item.getBook().getCategory().getCategoryName() : null)
                .stockQuantity(item.getBook().getStockQuantity())
                .originalPrice(originalPrice)
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .subTotal(subTotal)
                .build();
    }

    
    default BigDecimal calculateTotalPrice(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return BigDecimal.ZERO;

        return cartItems.stream()
                .map(item -> {
                    BigDecimal originalPrice = item.getBook().getPrice();
                    Integer discount = item.getBook().getDiscountPercentage();
                    BigDecimal unitPrice = originalPrice;

                    if (discount != null && discount > 0) {
                        BigDecimal discountFactor = BigDecimal.valueOf(100 - discount)
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        unitPrice = originalPrice.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
                    }
                    return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }
}