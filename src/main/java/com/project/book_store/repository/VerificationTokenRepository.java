package com.project.book_store.repository;

import com.project.book_store.entity.concretes.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUserEmailAndToken(String email, String token);

    Optional<VerificationToken> findByUserEmail(String email);

    List<VerificationToken> findAllByExpiryDateBefore(LocalDateTime twentyFourHoursAgo);
}
