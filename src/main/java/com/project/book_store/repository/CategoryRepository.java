package com.project.book_store.repository;

import com.project.book_store.entity.concretes.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);

    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
