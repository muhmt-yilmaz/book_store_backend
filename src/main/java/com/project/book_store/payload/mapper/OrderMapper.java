package com.project.book_store.payload.mapper;

import com.project.book_store.entity.concretes.Customer;
import com.project.book_store.entity.concretes.Order;
import com.project.book_store.entity.concretes.OrderItem;
import com.project.book_store.payload.response.OrderItemResponse;
import com.project.book_store.payload.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.email", target = "customerEmail")
    @Mapping(target = "customerName", expression = "java(fullName(order.getCustomer()))")
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "book.coverImageUrl", target = "coverImageUrl")
    @Mapping(source = "book.author.name", target = "authorName")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    default String fullName(Customer customer) {
        if (customer == null) return null;
        String name = customer.getName() != null ? customer.getName() : "";
        String surname = customer.getSurname() != null ? customer.getSurname() : "";
        String combined = (name + " " + surname).trim();
        return combined.isEmpty() ? null : combined;
    }
}