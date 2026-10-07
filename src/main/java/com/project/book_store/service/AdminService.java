package com.project.book_store.service;

import com.project.book_store.entity.concretes.Admin;
import com.project.book_store.entity.enums.RoleType;
import com.project.book_store.exception.ConflictException;
import com.project.book_store.payload.request.AdminRequest;
import com.project.book_store.payload.response.AdminResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.repository.AdminRepository;
import com.project.book_store.utils.FieldControl;
import com.project.book_store.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final FieldControl fieldControl;

    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public ResponseMessage save(AdminRequest request) {

        fieldControl.checkDuplicate(request.getEmail(),
                request.getTc(),
                request.getPhoneNumber());

        Admin admin = createAdminForSave(request);
        admin.setBuilt_in(false);

        if(Objects.equals(request.getEmail(), Messages.BUILT_IN_ADMIN_EMAIL)) admin.setBuilt_in(true);

        admin.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedData = adminRepository.save(admin);

        return ResponseMessage.<AdminResponse>builder().
                message("Admin saved").
                httpStatus(HttpStatus.CREATED).
                object(createResponse(savedData)). 
                        build();
    }

    protected Admin createAdminForSave(AdminRequest request){
        return Admin.builder().
                email(request.getEmail()).
                name(request.getName()).
                surname(request.getSurname()).
                password(request.getPassword()).
                tc(request.getTc()).
                birthDate(request.getBirthDate()).
                phoneNumber(request.getPhoneNumber()).
                gender(request.getGender()).
                isEnabled(request.isEnabled()).
                build();
    }

    private AdminResponse createResponse(Admin admin){
        return AdminResponse.builder().
                userId(admin.getId()).
                email(admin.getEmail()).
                name(admin.getName()).
                surname(admin.getSurname()).
                birthDate(admin.getBirthDate()).
                tc(admin.getTc()).
                phoneNumber(admin.getPhoneNumber()).
                gender(admin.getGender()).
                isEnabled(admin.isEnabled()).
                build();
    }

    public Page<AdminResponse> getAllAdmin(Pageable pageable) {
        return adminRepository.findAll(pageable).map(this::createResponse);
    }


    public String deleteAdmin(Long id) {

        Optional<Admin> admin = adminRepository.findById(id);

        if(admin.isPresent() && admin.get().isBuilt_in()){
            throw new ConflictException(Messages.NOT_PERMITTED_METHOD_MESSAGE);
        }

        if(admin.isPresent()){
            adminRepository.deleteById(id);

            return "Admin is deleted Successfully";
        }

        return Messages.NOT_FOUND_USER_MESSAGE;
    }

    public long countAllAdmin() {
        return adminRepository.count();
    }
}
