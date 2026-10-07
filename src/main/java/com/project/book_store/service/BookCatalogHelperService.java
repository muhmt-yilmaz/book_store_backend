package com.project.book_store.service;

import com.project.book_store.entity.concretes.Author;
import com.project.book_store.entity.concretes.Category;
import com.project.book_store.entity.concretes.Publisher;
import com.project.book_store.repository.AuthorRepository;
import com.project.book_store.repository.CategoryRepository;
import com.project.book_store.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BookCatalogHelperService {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;

    public Author getOrCreateAuthorByFullName(String rawFullName) {
        return getOrCreateAuthorByFullName(rawFullName, null, null);
    }


    
    public Author getOrCreateAuthorByFullName(String rawFullName, java.util.Date birthDate, String biography) {
        String cleanAuthor = normalizeText(rawFullName != null && !rawFullName.isBlank() ? rawFullName : "Bilinmeyen Yazar");


        
        
        
        int lastSpace = cleanAuthor.lastIndexOf(' ');
        String firstPart = lastSpace > 0 ? cleanAuthor.substring(0, lastSpace) : cleanAuthor;
        String lastPart = lastSpace > 0 ? cleanAuthor.substring(lastSpace + 1) : "-";

        return authorRepository.findByNameIgnoreCaseAndSurnameIgnoreCase(firstPart, lastPart)
                .orElseGet(() -> authorRepository.save(
                        Author.builder()
                                .name(firstPart)
                                .surname(lastPart)
                                .birthDate(birthDate)
                                .biography(biography)
                                .build()
                ));
    }

    public Publisher getOrCreatePublisherByName(String rawName) {
        String cleanPublisher = normalizeText(rawName != null && !rawName.isBlank() ? rawName : "Bilinmeyen Yayınevi");

        return publisherRepository.findByName(cleanPublisher)
                .orElseGet(() -> publisherRepository.save(Publisher.builder().name(cleanPublisher).build()));
    }

    
    public Category getOrCreateCategoryByName(String rawName) {
        String cleanCategory = normalizeText(rawName != null && !rawName.isBlank() ? rawName : "Genel");

        List<Category> existingCategories = categoryRepository.findAll();
        for (Category existing : existingCategories) {
            double similarity = calculateSimilarity(existing.getCategoryName(), cleanCategory);

            
            if (similarity >= 0.85) {
                return existing;
            }
        }

        
        return categoryRepository.save(Category.builder().categoryName(cleanCategory).build());
    }

    
    public String normalizeText(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        text = text.trim();
        String[] words = text.split("\\s+");
        StringBuilder normalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                normalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return normalized.toString().trim();
    }

    
    private double calculateSimilarity(String s1, String s2) {
        String longer = s1.toLowerCase(), shorter = s2.toLowerCase();
        if (s1.length() < s2.length()) { longer = s2.toLowerCase(); shorter = s1.toLowerCase(); }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; }

        int[] costs = new int[shorter.length() + 1];
        for (int i = 0; i <= shorter.length(); i++) { costs[i] = i; }
        for (int i = 1; i <= longer.length(); i++) {
            int nw = costs[0]; costs[0] = i;
            for (int j = 1; j <= shorter.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        longer.charAt(i - 1) == shorter.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j]; costs[j] = cj;
            }
        }
        int editDistance = costs[shorter.length()];
        return (longerLength - editDistance) / (double) longerLength;
    }
}
