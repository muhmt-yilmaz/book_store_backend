package com.project.book_store.service;

import com.project.book_store.entity.concretes.Book;
import com.project.book_store.entity.concretes.Customer;
import com.project.book_store.entity.concretes.Order;
import com.project.book_store.entity.concretes.OrderItem;
import com.project.book_store.entity.enums.OrderStatus;
import com.project.book_store.payload.mapper.OrderMapper;
import com.project.book_store.payload.request.OrderItemRequest;
import com.project.book_store.payload.request.OrderRequest;
import com.project.book_store.payload.response.OrderResponse;
import com.project.book_store.repository.BookRepository;
import com.project.book_store.repository.CustomerRepository;
import com.project.book_store.repository.OrderRepository;
import com.project.book_store.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final CartService cartService;
    private final PaymentService paymentService;

    
    @Transactional
    public OrderResponse createOrder(String email, OrderRequest orderRequest) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(Messages.CUSTOMER_NOT_FOUND));

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .customer(customer)
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .quantity(0)
                .recipientName(orderRequest.getRecipientName())
                .shippingPhone(orderRequest.getShippingPhone())
                .shippingAddress(orderRequest.getShippingAddress())
                .build();

        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        int totalOrderQuantity = 0;

        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new RuntimeException(Messages.BOOK_NOT_FOUND + itemRequest.getBookId()));

            if (book.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException(Messages.INSUFFICIENT_STOCK + book.getStockQuantity());
            }

            book.setStockQuantity(book.getStockQuantity() - itemRequest.getQuantity());
            bookRepository.save(book);

            BigDecimal bookPrice = book.getPrice();
            if (book.getDiscountPercentage() != null && book.getDiscountPercentage() > 0) {
                BigDecimal discountMultiplier = BigDecimal.valueOf(100 - book.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
                bookPrice = bookPrice.multiply(discountMultiplier);
            }

            BigDecimal itemTotalPrice = bookPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalOrderPrice = totalOrderPrice.add(itemTotalPrice);
            totalOrderQuantity += itemRequest.getQuantity();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .book(book)
                    .quantity(itemRequest.getQuantity())
                    .price(bookPrice)
                    .build();

            order.getOrderItems().add(orderItem);
        }

        order.setTotalPrice(totalOrderPrice);
        order.setQuantity(totalOrderQuantity);


        
        PaymentService.PaymentResult paymentResult = paymentService.chargeForOrder(customer, order, orderRequest, totalOrderPrice);
        if (!paymentResult.isSuccess()) {
            throw new RuntimeException("Ödeme başarısız: " + paymentResult.getErrorMessage());
        }
        order.setPaymentId(paymentResult.getPaymentId());

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(email);

        return orderMapper.toOrderResponse(savedOrder);
    }

    
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(Messages.CUSTOMER_NOT_FOUND));

        List<Order> orders = orderRepository.findByCustomerId(customer.getId());
        return orders.stream().map(orderMapper::toOrderResponse).toList();
    }

    
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerIdForAdmin(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException(Messages.CUSTOMER_NOT_FOUND);
        }

        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream().map(orderMapper::toOrderResponse).toList();
    }

    
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(Messages.ORDER_NOT_FOUND));
        return orderMapper.toOrderResponse(order);
    }

    
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(Messages.ORDER_NOT_FOUND));

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponse(savedOrder);
    }
}