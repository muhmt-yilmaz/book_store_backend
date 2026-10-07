package com.project.book_store.service;

import com.project.book_store.entity.concretes.Customer;
import com.project.book_store.entity.enums.RoleType;
import com.project.book_store.payload.mapper.CustomerMapper;
import com.project.book_store.payload.request.CustomerRequest;
import com.project.book_store.payload.request.ProfileUpdateRequest;
import com.project.book_store.payload.response.CustomerResponse;
import com.project.book_store.repository.CustomerRepository;
import com.project.book_store.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;

    
    public void save(CustomerRequest request) {


        
        
        Customer customer = Customer.builder()
                .email(request.getEmail())
                .tc(request.getTc())
                .name(request.getName())
                .surname(request.getSurname())
                .birthDate(request.getBirthDate())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .email(request.getEmail())
                .userRole(userRoleService.getUserRole(RoleType.CUSTOMER))
                .orders(new ArrayList<>()) 
                .build();


        


        customerRepository.save(customer);
    }

    
    public long countAllCustomer() {
        return customerRepository.count();
    }

    
    public CustomerResponse getProfile(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(Messages.CUSTOMER_NOT_FOUND));

        return customerMapper.toCustomerResponse(customer);
    }

    
    @Transactional
    public CustomerResponse updateProfile(String email, ProfileUpdateRequest request) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(Messages.CUSTOMER_NOT_FOUND));

        customer.setName(request.getName());
        customer.setSurname(request.getSurname());
        customer.setBirthDate(request.getBirthDate());
        customer.setGender(request.getGender());

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            customer.setPhoneNumber(request.getPhoneNumber());
        }

        Customer updated = customerRepository.save(customer);
        return customerMapper.toCustomerResponse(updated);
    }
}
