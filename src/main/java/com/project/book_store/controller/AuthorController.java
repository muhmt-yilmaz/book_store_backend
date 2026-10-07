package com.project.book_store.controller;

import com.project.book_store.payload.request.AuthorRequest;
import com.project.book_store.payload.response.AuthorDetailResponse;
import com.project.book_store.payload.response.AuthorResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.AuthorService;
import com.project.book_store.utils.Messages;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<ResponseMessage<Page<AuthorResponse>>> getAllWithPages(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type
    ) {
        Sort.Direction direction = type.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<AuthorResponse> authors = authorService.getAllAuthors(pageable);

        ResponseMessage<Page<AuthorResponse>> response = ResponseMessage.<Page<AuthorResponse>>builder()
                .object(authors)
                .message(Messages.AUTHOR_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<AuthorResponse>>> getAllWithList(){

        List<AuthorResponse> authors = authorService.getAllList();

        ResponseMessage<List<AuthorResponse>> response = ResponseMessage.<List<AuthorResponse>>builder()
                .object(authors)
                .message(Messages.AUTHOR_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<AuthorResponse>> getAuthorById(@PathVariable Long id) {
        AuthorResponse author = authorService.getAuthorById(id);

        ResponseMessage<AuthorResponse> response = ResponseMessage.<AuthorResponse>builder()
                .object(author)
                .message(Messages.AUTHOR_FOUND_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/{id}/books")
    public ResponseEntity<ResponseMessage<AuthorDetailResponse>> getAuthorWithBooks(@PathVariable Long id) {
        AuthorDetailResponse response = authorService.getAuthorDetailWithBooks(id);

        ResponseMessage<AuthorDetailResponse> body = ResponseMessage.<AuthorDetailResponse>builder()
                .object(response)
                .message(Messages.AUTHOR_BOOKS_FETCHED_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(body);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<AuthorResponse>> createAuthor(@RequestBody @Valid AuthorRequest authorRequest) {
        AuthorResponse createdAuthor = authorService.createAuthor(authorRequest);

        ResponseMessage<AuthorResponse> response = ResponseMessage.<AuthorResponse>builder()
                .object(createdAuthor)
                .message(Messages.AUTHOR_CREATED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<AuthorResponse>> updateAuthor(
            @PathVariable Long id,
            @RequestBody @Valid AuthorRequest authorRequest) {

        AuthorResponse updatedAuthor = authorService.updateAuthor(id, authorRequest);

        ResponseMessage<AuthorResponse> response = ResponseMessage.<AuthorResponse>builder()
                .object(updatedAuthor)
                .message(Messages.AUTHOR_UPDATED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<String>> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);

        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.AUTHOR_DELETED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}