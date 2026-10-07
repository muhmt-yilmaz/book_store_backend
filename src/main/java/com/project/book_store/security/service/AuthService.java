package com.project.book_store.security.service;

import com.project.book_store.entity.abstracts.User;
import com.project.book_store.entity.concretes.Customer;
import com.project.book_store.entity.concretes.UserRole;
import com.project.book_store.entity.concretes.VerificationToken;
import com.project.book_store.entity.enums.RoleType;
import com.project.book_store.payload.mapper.CustomerMapper;
import com.project.book_store.payload.request.CustomerRequest;
import com.project.book_store.payload.request.LoginRequest;
import com.project.book_store.payload.request.VerifyRequest;
import com.project.book_store.payload.response.AuthResponse;
import com.project.book_store.payload.response.CustomerResponse;
import com.project.book_store.repository.CustomerRepository;
import com.project.book_store.repository.UserRoleRepository;
import com.project.book_store.repository.VerificationTokenRepository;
import com.project.book_store.security.jwt.JwtUtils;
import com.project.book_store.service.EmailService;
import com.project.book_store.utils.Messages; 
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    
    private static final long CODE_EXPIRY_MINUTES = 3;
    private static final long RESEND_COOLDOWN_SECONDS = 60;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    private final CustomerRepository customerRepository;
    private final UserRoleRepository userRoleRepository;
    private final CustomerMapper customerMapper;

    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();


        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_CUSTOMER");

        return AuthResponse.builder()
                .username(userDetails.getUsername())
                .name(userDetails.getName())
                .token(token)
                .role(role)
                .build();
    }

    @Transactional
    public CustomerResponse registerUser(CustomerRequest request) {
        
        if (request.getBirthDate().isAfter(java.time.LocalDate.now().minusYears(18))) {
            throw new RuntimeException(Messages.AGE_RESTRICTION_ERROR);
        }

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(Messages.EMAIL_ALREADY_EXISTS);
        }

        Customer customer = customerMapper.mapToCustomer(request);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        UserRole customerRole = userRoleRepository.findByERoleEquals(RoleType.CUSTOMER)
                .orElseThrow(() -> new RuntimeException(Messages.ROLE_NOT_FOUND));
        customer.setUserRole(customerRole);

        Customer savedCustomer = customerRepository.save(customer);

        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        VerificationToken token = VerificationToken.builder()
                .token(verificationCode)
                .user(savedCustomer)
                .expiryDate(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES))
                .build();

        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(savedCustomer.getEmail(), verificationCode, (int) CODE_EXPIRY_MINUTES);

        return customerMapper.toCustomerResponse(savedCustomer);
    }

    @Transactional
    public String verifyEmail(VerifyRequest request) {
        VerificationToken verificationToken = verificationTokenRepository
                .findByUserEmailAndToken(request.getEmail(), request.getCode())
                .orElseThrow(() -> new RuntimeException(Messages.INVALID_VERIFICATION_CODE));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new RuntimeException(Messages.VERIFICATION_CODE_EXPIRED);
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);

        verificationTokenRepository.delete(verificationToken);

        return Messages.VERIFICATION_SUCCESS;
    }

    @Transactional
    public void resendVerificationEmail(String email) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(Messages.USER_NOT_FOUND));

        if (customer.isEnabled()) {
            throw new RuntimeException(Messages.ACCOUNT_ALREADY_VERIFIED);
        }


        
        verificationTokenRepository.findByUserEmail(email).ifPresent(existingToken -> {
            long secondsSinceLastSend = Duration.between(existingToken.getCreatedAt(), LocalDateTime.now()).getSeconds();
            if (secondsSinceLastSend < RESEND_COOLDOWN_SECONDS) {
                long secondsRemaining = RESEND_COOLDOWN_SECONDS - secondsSinceLastSend;
                throw new RuntimeException(String.format(Messages.RESEND_COOLDOWN_MESSAGE, secondsRemaining));
            }
        });

        verificationTokenRepository.findByUserEmail(email).ifPresent(verificationTokenRepository::delete);

        String newCode = String.format("%06d", new Random().nextInt(999999));

        VerificationToken newToken = VerificationToken.builder()
                .token(newCode)
                .user(customer)
                .expiryDate(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES))
                .build();

        verificationTokenRepository.save(newToken);

        emailService.sendVerificationEmail(customer.getEmail(), newCode, (int) CODE_EXPIRY_MINUTES);
    }
}