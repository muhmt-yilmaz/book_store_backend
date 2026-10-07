package com.project.book_store.controller;

import com.project.book_store.payload.request.AdminRequest;
import com.project.book_store.payload.response.AdminResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.AdminService;
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
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    public final AdminService adminService;


    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> save(@RequestBody @Valid AdminRequest adminRequest){

        return ResponseEntity.ok(adminService.save(adminRequest));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<Page<AdminResponse>>> getAll(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        Page<AdminResponse> admins = adminService.getAllAdmin(pageable);

        ResponseMessage<Page<AdminResponse>> response = ResponseMessage.<Page<AdminResponse>>builder()
                .object(admins)
                .message(Messages.ADMIN_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id){

        return ResponseEntity.ok(adminService.deleteAdmin(id));

    }


}
