package com.project.book_store.service;

import com.project.book_store.entity.concretes.Manager;
import com.project.book_store.entity.enums.RoleType;
import com.project.book_store.payload.request.ManagerRequest;
import com.project.book_store.payload.response.ManagerResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.repository.ManagerRepository;
import com.project.book_store.utils.FieldControl;
import com.project.book_store.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;

    private final FieldControl fieldControl;

    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public ResponseMessage<ManagerResponse> save(ManagerRequest request) {

        fieldControl.checkDuplicate(request.getEmail(),
                request.getTc(),
                request.getPhoneNumber());

        Manager manager = createManagerForSave(request);


        
        manager.setEnabled(true);

        manager.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));

        manager.setPassword(passwordEncoder.encode(manager.getPassword()));

        Manager savedData = managerRepository.save(manager);

        return ResponseMessage.<ManagerResponse>builder()
                .message(Messages.MANAGER_SAVED_MESSAGE)
                .httpStatus(HttpStatus.CREATED)
                .object(createResponse(savedData))
                .build();
    }

    protected Manager createManagerForSave(ManagerRequest request) {
        return Manager.builder()
                .email(request.getEmail())
                .name(request.getName())
                .surname(request.getSurname())
                .password(request.getPassword())
                .tc(request.getTc())
                .birthDate(request.getBirthDate())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .build();
    }

    private ManagerResponse createResponse(Manager manager) {
        return ManagerResponse.builder()
                .userId(manager.getId())
                .email(manager.getEmail())
                .name(manager.getName())
                .surname(manager.getSurname())
                .birthDate(manager.getBirthDate())
                .tc(manager.getTc())
                .phoneNumber(manager.getPhoneNumber())
                .gender(manager.getGender())
                .isEnabled(manager.isEnabled())
                .build();
    }

    public Page<ManagerResponse> getAllManager(Pageable pageable) {
        return managerRepository.findAll(pageable).map(this::createResponse);
    }

    public String deleteManager(Long id) {

        Optional<Manager> manager = managerRepository.findById(id);

        if (manager.isPresent()) {
            managerRepository.deleteById(id);
            return "Manager is deleted Successfully";
        }

        return Messages.NOT_FOUND_USER_MESSAGE;
    }

    public long countAllManager() {
        return managerRepository.count();
    }
}
