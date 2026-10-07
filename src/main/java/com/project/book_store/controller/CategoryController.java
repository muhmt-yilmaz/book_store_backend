package com.project.book_store.controller;

import com.project.book_store.payload.request.CategoryRequest;
import com.project.book_store.payload.response.CategoryResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.CategoryService;
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
@RequestMapping("/categories") 
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseMessage<Page<CategoryResponse>>> getAllWithPages(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "categoryName") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type
    ) {
        Sort.Direction direction = type.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<CategoryResponse> categories = categoryService.getAllCategories(pageable);

        ResponseMessage<Page<CategoryResponse>> response = ResponseMessage.<Page<CategoryResponse>>builder()
                .object(categories)
                .message(Messages.CATEGORY_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<CategoryResponse>>> getAllWithList(){

        List<CategoryResponse> categories = categoryService.getAllList();

        ResponseMessage<List<CategoryResponse>> response = ResponseMessage.<List<CategoryResponse>>builder()
                .object(categories)
                .message(Messages.CATEGORY_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);

        ResponseMessage<CategoryResponse> response = ResponseMessage.<CategoryResponse>builder()
                .object(category)
                .message(Messages.CATEGORY_FOUND_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<CategoryResponse>> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        CategoryResponse createdCategory = categoryService.createCategory(categoryRequest);

        ResponseMessage<CategoryResponse> response = ResponseMessage.<CategoryResponse>builder()
                .object(createdCategory)
                .message(Messages.CATEGORY_CREATED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest categoryRequest) {

        CategoryResponse updatedCategory = categoryService.updateCategory(id, categoryRequest);

        ResponseMessage<CategoryResponse> response = ResponseMessage.<CategoryResponse>builder()
                .object(updatedCategory)
                .message(Messages.CATEGORY_UPDATED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);

        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.CATEGORY_DELETED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}