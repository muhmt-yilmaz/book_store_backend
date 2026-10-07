package com.project.book_store.repository;

import com.project.book_store.entity.concretes.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Book> {
    boolean existsByIsbn(String isbn);

    boolean existsBySku(String sku);

    boolean existsByTitleIgnoreCaseAndAuthorNameIgnoreCase(String title, String authorName);
    List<Book> findByTitleContainingIgnoreCase(String title);

    
    boolean existsByIsbnAndIdNot(String isbn, Long id);
    boolean existsBySkuAndIdNot(String sku, Long id);
    boolean existsByTitleIgnoreCaseAndAuthorNameIgnoreCaseAndIdNot(String title, String authorName, Long id);

    
    List<Book> findByAuthorId(Long authorId);

    
    long countByCategoryId(Long categoryId);
}
