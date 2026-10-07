package com.project.book_store.controller;

import com.project.book_store.payload.request.ManagerRequest;
import com.project.book_store.payload.response.ManagerResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.ManagerService;
import com.project.book_store.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<ManagerResponse>> save(@RequestBody @Valid ManagerRequest managerRequest) {

        return ResponseEntity.ok(managerService.save(managerRequest));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<Page<ManagerResponse>>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        Page<ManagerResponse> managers = managerService.getAllManager(pageable);

        ResponseMessage<Page<ManagerResponse>> response = ResponseMessage.<Page<ManagerResponse>>builder()
                .object(managers)
                .message(Messages.MANAGER_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        return ResponseEntity.ok(managerService.deleteManager(id));
    }
}
