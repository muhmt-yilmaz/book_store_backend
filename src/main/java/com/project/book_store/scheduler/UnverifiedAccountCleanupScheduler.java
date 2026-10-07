package com.project.book_store.scheduler;

import com.project.book_store.entity.concretes.VerificationToken;
import com.project.book_store.repository.CustomerRepository;
import com.project.book_store.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UnverifiedAccountCleanupScheduler {

    private final VerificationTokenRepository tokenRepository;
    private final CustomerRepository customerRepository;

    
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanUpUnverifiedAccounts() {
        
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        
        List<VerificationToken> expiredTokens = tokenRepository.findAllByExpiryDateBefore(twentyFourHoursAgo);

        for (VerificationToken token : expiredTokens) {
            if (!token.getUser().isEnabled()) {
                
                tokenRepository.delete(token);
                customerRepository.deleteById(token.getUser().getId());
                System.out.println("Doğrulanmamış hesap silindi: " + token.getUser().getEmail());
            }
        }
    }
}