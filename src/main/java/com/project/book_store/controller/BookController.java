package com.project.book_store.controller;

import com.project.book_store.entity.concretes.Book;
import com.project.book_store.payload.request.BookRequest;
import com.project.book_store.payload.response.BookResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.BookService;
import com.project.book_store.service.GoogleBooksService;
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
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final GoogleBooksService googleBooksService;
    private final BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<ResponseMessage<List<BookResponse>>> searchBooks(@RequestParam String q) {

        List<Book> result = googleBooksService.smartSearch(q);

        ResponseMessage<List<BookResponse>> response = ResponseMessage.<List<BookResponse>>builder()
                .object(bookService.toResponseList(result))
                .message("Arama sonuçları başarıyla getirildi.")
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<Page<BookResponse>>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "authorId", required = false) Long authorId,
            @RequestParam(value = "publisherId", required = false) Long publisherId,
            @RequestParam(value = "q", required = false) String q
    ) {
        Sort.Direction direction = type.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));


        
        
        Page<BookResponse> books = (categoryId == null && authorId == null && publisherId == null && (q == null || q.isBlank()))
                ? bookService.getAllBooks(pageable)
                : bookService.getFilteredBooks(categoryId, authorId, publisherId, q, pageable);

        ResponseMessage<Page<BookResponse>> response = ResponseMessage.<Page<BookResponse>>builder()
                .object(books)
                .message(Messages.BOOK_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);

        ResponseMessage<BookResponse> response = ResponseMessage.<BookResponse>builder()
                .object(book)
                .message(Messages.BOOK_FOUND_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<BookResponse>> createBook(@RequestBody @Valid BookRequest bookRequest) {
        BookResponse createdBook = bookService.createBook(bookRequest);

        ResponseMessage<BookResponse> response = ResponseMessage.<BookResponse>builder()
                .object(createdBook)
                .message(Messages.BOOK_CREATED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<BookResponse>> updateBook(
            @PathVariable Long id,
            @RequestBody @Valid BookRequest bookRequest) {

        BookResponse updatedBook = bookService.updateBook(id, bookRequest);

        ResponseMessage<BookResponse> response = ResponseMessage.<BookResponse>builder()
                .object(updatedBook)
                .message(Messages.BOOK_UPDATED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<String>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);

        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.BOOK_DELETED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}
