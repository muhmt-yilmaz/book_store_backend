package com.project.book_store.repository;

import com.project.book_store.entity.concretes.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByName(String name);

    boolean existsByNameIgnoreCase(String name);
}
