package com.project.book_store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {


    
    
    @GetMapping
    public Map<String, String> home() {
        return Map.of(
                "status", "UP",
                "message", "Book Store API çalışıyor."
        );
    }
}