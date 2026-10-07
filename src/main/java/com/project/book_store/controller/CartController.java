package com.project.book_store.controller;

import com.project.book_store.payload.request.CartItemRequest;
import com.project.book_store.payload.response.CartResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.CartService;
import com.project.book_store.utils.Messages;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    
    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<CartResponse>> getCart(Principal principal) {
        String email = principal.getName();
        CartResponse cartResponse = cartService.getCartResponse(email);

        ResponseMessage<CartResponse> response = ResponseMessage.<CartResponse>builder()
                .object(cartResponse)
                .message("Sepet başarıyla getirildi.")
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<CartResponse>> addToCart(
            Principal principal,
            @RequestBody @Valid CartItemRequest request) {

        String email = principal.getName();
        CartResponse cartResponse = cartService.addToCart(email, request);

        ResponseMessage<CartResponse> response = ResponseMessage.<CartResponse>builder()
                .object(cartResponse)
                .message("Ürün sepete eklendi.")
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/merge")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<CartResponse>> mergeCart(
            Principal principal,
            @RequestBody List<CartItemRequest> guestCartItems) {

        String email = principal.getName();
        CartResponse cartResponse = cartService.mergeGuestCart(email, guestCartItems);

        ResponseMessage<CartResponse> response = ResponseMessage.<CartResponse>builder()
                .object(cartResponse)
                .message("Sepetiniz senkronize edildi.")
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    
    @PutMapping("/item/{cartItemId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<CartResponse>> updateQuantity(
            Principal principal,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {

        String email = principal.getName();
        CartResponse cartResponse = cartService.updateItemQuantity(email, cartItemId, quantity);

        ResponseMessage<CartResponse> response = ResponseMessage.<CartResponse>builder()
                .object(cartResponse)
                .message("Sepet güncellendi.")
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/item/{cartItemId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<CartResponse>> removeFromCart(
            Principal principal,
            @PathVariable Long cartItemId) {

        String email = principal.getName();
        CartResponse cartResponse = cartService.removeFromCart(email, cartItemId);

        ResponseMessage<CartResponse> response = ResponseMessage.<CartResponse>builder()
                .object(cartResponse)
                .message("Ürün sepetten kaldırıldı.")
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}