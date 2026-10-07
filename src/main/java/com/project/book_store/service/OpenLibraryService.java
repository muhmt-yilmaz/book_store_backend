package com.project.book_store.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.book_store.entity.concretes.Author;
import com.project.book_store.entity.concretes.Book;
import com.project.book_store.entity.concretes.Category;
import com.project.book_store.entity.concretes.Publisher;
import com.project.book_store.repository.AuthorRepository;
import com.project.book_store.repository.BookRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OpenLibraryService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCatalogHelperService catalogHelper;
    private final RestTemplate restTemplate;

    @Value("${openlibrary.api.url:https://openlibrary.org}")
    private String baseUrl;

    @Value("${openlibrary.api.results-per-page:100}")
    private int resultsPerPage;

    @Value("${app.store.sku.prefix:BKS}")
    private String skuPrefix;


    
    
    private static final int MAX_AUTHOR_LOOKUPS_PER_PAGE = 5;


    
    static final String[] SUBJECTS = {
            "fiction", "fantasy", "science_fiction", "mystery_and_detective_stories",
            "love_stories", "history", "philosophy", "psychology", "biography",
            "poetry", "adventure", "self-help", "business", "horror", "thriller",
            "juvenile_fiction", "young_adult_fiction", "cooking", "art", "classic_literature"
    };

    public int resultsPerPage() {
        return resultsPerPage;
    }

    @Transactional
    public GoogleBooksService.SyncResult fetchOnePage(String subject, int offset) {
        SubjectResponse response = callOpenLibraryApi(subject, offset);
        if (response == null || response.getWorks() == null || response.getWorks().isEmpty()) {
            return new GoogleBooksService.SyncResult();
        }
        return saveBooksFromResponse(response);
    }

    private SubjectResponse callOpenLibraryApi(String subject, int offset) {
        String requestUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/subjects/{subject}.json")
                .queryParam("limit", resultsPerPage)
                .queryParam("offset", offset)
                .buildAndExpand(subject)
                .toUriString();

        try {
            return restTemplate.exchange(requestUrl, HttpMethod.GET, buildRequestEntity(), SubjectResponse.class).getBody();
        } catch (Exception ex) {
            System.out.println("OPEN LIBRARY API HATASI (" + subject + "): " + ex.getMessage());
            return null;
        }
    }

    private HttpEntity<Void> buildRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        
        
        headers.set("User-Agent", "BookStoreApp/1.0 (admin contact not configured)");
        return new HttpEntity<>(headers);
    }


    
    
    
    private AuthorDetail fetchAuthorDetail(String authorKey) {
        if (authorKey == null || authorKey.isBlank()) return null;
        try {
            Thread.sleep(1000); 
            String url = baseUrl + authorKey + ".json";
            return restTemplate.exchange(url, HttpMethod.GET, buildRequestEntity(), AuthorDetail.class).getBody();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception ex) {
            System.out.println("OPEN LIBRARY YAZAR DETAYI HATASI (" + authorKey + "): " + ex.getMessage());
            return null;
        }
    }


    
    
    private Date parseFreeTextDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) return null;
        String cleaned = rawDate.trim();

        String[] patterns = {"d MMMM yyyy", "MMMM d, yyyy", "MMMM d yyyy", "MMMM yyyy", "yyyy"};
        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, java.util.Locale.ENGLISH);
                sdf.setLenient(false);
                return sdf.parse(cleaned);
            } catch (Exception ignored) {
                
            }
        }


        try {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(1[0-9]{3}|20[0-9]{2})").matcher(cleaned);
            if (m.find()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy", java.util.Locale.ENGLISH);
                return sdf.parse(m.group(1));
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private GoogleBooksService.SyncResult saveBooksFromResponse(SubjectResponse response) {
        GoogleBooksService.SyncResult result = new GoogleBooksService.SyncResult();
        Set<String> authorLookupsDoneThisPage = new HashSet<>();

        for (Work work : response.getWorks()) {
            String title = catalogHelper.normalizeText(work.getTitle());
            if (title.isBlank()) {
                result.skippedNoTitle++;
                continue;
            }


            


            if (work.getCoverId() == null) {
                result.skippedNoIsbn++; 
                continue;
            }

            String rawAuthor = null;
            String authorKey = null;
            if (work.getAuthors() != null && !work.getAuthors().isEmpty()) {
                rawAuthor = work.getAuthors().get(0).getName();
                authorKey = work.getAuthors().get(0).getKey();
            }

            Author authorObj = resolveAuthorWithOptionalDetail(rawAuthor, authorKey, authorLookupsDoneThisPage);
            Category categoryObj = catalogHelper.getOrCreateCategoryByName(response.getName());
            Publisher publisherObj = catalogHelper.getOrCreatePublisherByName(null); 

            if (bookRepository.existsByTitleIgnoreCaseAndAuthorNameIgnoreCase(title, authorObj.getName())) {
                result.skippedDuplicate++;
                continue;
            }

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(authorObj);
            book.setCategory(categoryObj);
            book.setPublisher(publisherObj);
            book.setPublicationYear(work.getFirstPublishYear());
            book.setCoverImageUrl("https://covers.openlibrary.org/b/id/" + work.getCoverId() + "-L.jpg");

            double randomPrice = 150.0 + (Math.random() * 200);
            book.setPrice(BigDecimal.valueOf(randomPrice));
            int randomStock = 10 + (int) (Math.random() * 90);
            book.setStockQuantity(randomStock);

            
            String workKey = work.getKey() != null ? work.getKey().replace("/works/", "") : UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            book.setSku(skuPrefix + "-OL-" + workKey);

            bookRepository.save(book);
            result.added++;
            System.out.println("BAŞARIYLA EKLENDİ (Open Library) -> SKU: " + book.getSku() + " | Başlık: " + title);
        }

        return result;
    }


    
    
    private Author resolveAuthorWithOptionalDetail(String rawAuthor, String authorKey, Set<String> authorLookupsDoneThisPage) {
        String normalizedName = catalogHelper.normalizeText(rawAuthor != null && !rawAuthor.isBlank() ? rawAuthor : "Bilinmeyen Yazar");
        int lastSpace = normalizedName.lastIndexOf(' ');
        String namePart = lastSpace > 0 ? normalizedName.substring(0, lastSpace) : normalizedName;
        String surnamePart = lastSpace > 0 ? normalizedName.substring(lastSpace + 1) : "-";

        boolean alreadyExists = authorRepository.findByNameIgnoreCaseAndSurnameIgnoreCase(namePart, surnamePart).isPresent();

        if (alreadyExists || authorKey == null || authorKey.isBlank()
                || authorLookupsDoneThisPage.size() >= MAX_AUTHOR_LOOKUPS_PER_PAGE) {
            return catalogHelper.getOrCreateAuthorByFullName(rawAuthor);
        }

        authorLookupsDoneThisPage.add(authorKey);
        AuthorDetail detail = fetchAuthorDetail(authorKey);

        Date birthDate = detail != null ? parseFreeTextDate(detail.getBirthDate()) : null;
        String biography = detail != null ? detail.getBioText() : null;

        return catalogHelper.getOrCreateAuthorByFullName(rawAuthor, birthDate, biography);
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubjectResponse {
        private String key;
        private String name;
        private List<Work> works;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Work {
        private String key;
        private String title;
        private List<WorkAuthor> authors;
        private Integer cover_id;
        private Integer first_publish_year;

        public Integer getCoverId() { return cover_id; }
        public Integer getFirstPublishYear() { return first_publish_year; }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkAuthor {
        private String name;
        private String key;
    }


    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorDetail {
        private String name;
        private String birth_date;
        private Object bio;

        public String getBirthDate() { return birth_date; }

        @SuppressWarnings("unchecked")
        public String getBioText() {
            if (bio == null) return null;
            if (bio instanceof String s) return s;
            if (bio instanceof java.util.Map<?, ?> map) {
                Object value = map.get("value");
                return value != null ? value.toString() : null;
            }
            return null;
        }
    }
}
