package com.project.book_store.controller;

import com.project.book_store.payload.request.PublisherRequest;
import com.project.book_store.payload.response.PublisherResponse;
import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.service.PublisherService;
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
@RequestMapping("/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<ResponseMessage<Page<PublisherResponse>>> getAllWithPages(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type
    ) {
        Sort.Direction direction = type.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<PublisherResponse> publishers = publisherService.getAllPublishers(pageable);

        ResponseMessage<Page<PublisherResponse>> response = ResponseMessage.<Page<PublisherResponse>>builder()
                .object(publishers)
                .message(Messages.PUBLISHER_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<PublisherResponse>>> getAllWithList(){

        List<PublisherResponse> publishers = publisherService.getAllList();

        ResponseMessage<List<PublisherResponse>> response = ResponseMessage.<List<PublisherResponse>>builder()
                .object(publishers)
                .message(Messages.PUBLISHER_LIST_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<PublisherResponse>> getPublisherById(@PathVariable Long id) {
        PublisherResponse publisher = publisherService.getPublisherById(id);

        ResponseMessage<PublisherResponse> response = ResponseMessage.<PublisherResponse>builder()
                .object(publisher)
                .message(Messages.PUBLISHER_FOUND_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<PublisherResponse>> createPublisher(@RequestBody @Valid PublisherRequest publisherRequest) {
        PublisherResponse createdPublisher = publisherService.createPublisher(publisherRequest);

        ResponseMessage<PublisherResponse> response = ResponseMessage.<PublisherResponse>builder()
                .object(createdPublisher)
                .message(Messages.PUBLISHER_CREATED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<PublisherResponse>> updatePublisher(
            @PathVariable Long id,
            @RequestBody @Valid PublisherRequest publisherRequest) {

        PublisherResponse updatedPublisher = publisherService.updatePublisher(id, publisherRequest);

        ResponseMessage<PublisherResponse> response = ResponseMessage.<PublisherResponse>builder()
                .object(updatedPublisher)
                .message(Messages.PUBLISHER_UPDATED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<String>> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);

        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.PUBLISHER_DELETED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}