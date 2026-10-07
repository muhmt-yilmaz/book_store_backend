package com.project.book_store.repository;

import com.project.book_store.entity.concretes.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String name);

    boolean existsByNameIgnoreCase(String name);


    Optional<Author> findByNameIgnoreCaseAndSurnameIgnoreCase(String name, String surname);

}
