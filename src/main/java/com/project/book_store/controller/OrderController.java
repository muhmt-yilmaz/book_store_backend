package com.project.book_store.controller;

import com.project.book_store.entity.enums.OrderStatus;
import com.project.book_store.payload.request.OrderRequest;
import com.project.book_store.payload.response.OrderResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.OrderService;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<OrderResponse>> createOrder(
            Principal principal,
            @RequestBody @Valid OrderRequest request) {

        String email = principal.getName();
        OrderResponse response = orderService.createOrder(email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseMessage.<OrderResponse>builder()
                        .object(response)
                        .message(Messages.ORDER_CREATED)
                        .httpStatus(HttpStatus.CREATED)
                        .build()
        );
    }

    
    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseMessage<List<OrderResponse>>> getMyOrders(Principal principal) {
        String email = principal.getName();
        List<OrderResponse> responses = orderService.getCustomerOrders(email);

        return ResponseEntity.ok(
                ResponseMessage.<List<OrderResponse>>builder()
                        .object(responses)
                        .message(Messages.ORDER_HISTORY_FETCHED)
                        .httpStatus(HttpStatus.OK)
                        .build()
        );
    }

    
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> responses = orderService.getAllOrders();

        return ResponseEntity.ok(
                ResponseMessage.<List<OrderResponse>>builder()
                        .object(responses)
                        .message(Messages.ALL_ORDERS_FETCHED)
                        .httpStatus(HttpStatus.OK)
                        .build()
        );
    }

    
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<List<OrderResponse>>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<OrderResponse> responses = orderService.getOrdersByCustomerIdForAdmin(customerId);

        return ResponseEntity.ok(
                ResponseMessage.<List<OrderResponse>>builder()
                        .object(responses)
                        .message(Messages.ORDER_HISTORY_FETCHED)
                        .httpStatus(HttpStatus.OK)
                        .build()
        );
    }

    
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<OrderResponse>> getOrderById(@PathVariable Long orderId) {
        OrderResponse response = orderService.getOrderByIdForAdmin(orderId);

        return ResponseEntity.ok(
                ResponseMessage.<OrderResponse>builder()
                        .object(response)
                        .message(Messages.ORDER_FETCHED)
                        .httpStatus(HttpStatus.OK)
                        .build()
        );
    }

    
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        OrderResponse response = orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(
                ResponseMessage.<OrderResponse>builder()
                        .object(response)
                        .message(Messages.ORDER_STATUS_UPDATED)
                        .httpStatus(HttpStatus.OK)
                        .build()
        );
    }
}