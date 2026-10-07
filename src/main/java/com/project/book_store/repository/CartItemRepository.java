package com.project.book_store.repository;

import com.project.book_store.entity.concretes.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
