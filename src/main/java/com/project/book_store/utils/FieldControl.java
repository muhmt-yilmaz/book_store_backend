package com.project.book_store.utils;

import com.project.book_store.exception.ConflictException;
import com.project.book_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FieldControl {

    
    private final UserRepository userRepository;

    
    public void checkDuplicate(String email, String tc, String phone) {

        
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_EMAIL, email));
        }

        if (userRepository.existsByTc(tc)) {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_TC, tc));
        }

        if (userRepository.existsByPhoneNumber(phone)) {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_PHONE_NUMBER, phone));
        }
    }
}