package com.project.book_store.repository;

import com.project.book_store.entity.abstracts.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByTc(String tc);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
}
