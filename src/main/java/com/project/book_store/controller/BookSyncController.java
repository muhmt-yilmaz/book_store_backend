package com.project.book_store.controller;

import com.project.book_store.service.BulkBookSeedService;
import com.project.book_store.service.GoogleBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookSyncController {

    private final GoogleBooksService googleBooksService;
    private final BulkBookSeedService bulkBookSeedService;

    
    @PostMapping("/sync")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<String> syncBooksFromGoogle(@RequestParam String query) {
        ResponseEntity<String> result = googleBooksService.fetchAndSaveBooks(query);
        return ResponseEntity.ok(result.getBody());
    }


    
    @PostMapping("/bulk-sync")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<String> bulkSyncBooksFromGoogle(@RequestParam(defaultValue = "40") int count) {
        return bulkBookSeedService.bulkFetchNewBooks(count);
    }
}