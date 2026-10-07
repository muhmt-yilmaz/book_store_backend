package com.project.book_store.security.service;

import com.project.book_store.entity.concretes.*;
import com.project.book_store.repository.*;
import com.project.book_store.utils.Messages;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            return UserDetailsImpl.build(customer.get());
        }

        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return UserDetailsImpl.build(admin.get());
        }

        Optional<Manager> manager = managerRepository.findByEmail(email);
        if (manager.isPresent()) {
            return UserDetailsImpl.build(manager.get());
        }

        throw new UsernameNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,email));
    }
}
