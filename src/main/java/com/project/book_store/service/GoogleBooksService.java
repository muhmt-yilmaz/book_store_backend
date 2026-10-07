package com.project.book_store.service;

import com.project.book_store.entity.concretes.*;
import com.project.book_store.payload.response.GoogleBooksResponse;
import com.project.book_store.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleBooksService {

    private final BookRepository bookRepository;
    private final BookCatalogHelperService catalogHelper;
    private final RestTemplate restTemplate;

    @Value("${google.books.api.url}")
    private String apiUrl;

    @Value("${google.books.api.key}")
    private String apiKey;

    @Value("${google.books.api.max-results:40}")
    private int maxResults;

    @Value("${google.books.api.lang-restrict:tr}")
    private String langRestrict;

    @Value("${google.books.api.print-type:books}")
    private String printType;

    @Value("${app.store.sku.prefix:BKS}")
    private String skuPrefix;


    
    static final String[] BULK_FETCH_SUBJECTS = {
            "subject:fiction", "subject:fantasy", "subject:science fiction", "subject:mystery",
            "subject:romance", "subject:history", "subject:philosophy", "subject:psychology",
            "subject:biography", "subject:poetry", "subject:classic literature", "subject:adventure",
            "subject:self-help", "subject:business", "subject:horror", "subject:thriller",
            "subject:children", "subject:young adult fiction", "subject:cooking", "subject:art"
    };

    
    private static final int RESULTS_PER_PAGE = 40;

    @Transactional
    public ResponseEntity<String> fetchAndSaveBooks(String query) {
        GoogleBooksResponse response = callGoogleBooksApi(query, 0);

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return ResponseEntity.ok("\"" + query + "\" araması için Google Books'ta hiç sonuç bulunamadı.");
        }

        SyncResult result = saveBooksFromResponse(response, false);


        
        StringBuilder resultStr = new StringBuilder();
        resultStr.append("\"").append(query).append("\" araması tamamlandı: ")
                .append(result.added).append(" yeni kitap eklendi");
        if (result.skippedDuplicate > 0) {
            resultStr.append(", ").append(result.skippedDuplicate).append(" kitap zaten mevcut olduğu için atlandı");
        }
        if (result.skippedNoTitle > 0) {
            resultStr.append(", ").append(result.skippedNoTitle).append(" kayıt başlık bilgisi olmadığı için atlandı");
        }
        resultStr.append(".");

        return ResponseEntity.ok(resultStr.toString());
    }


    
    @Transactional
    public SyncResult fetchOnePage(String query, int startIndex, boolean requireIsbn) {
        GoogleBooksResponse response = callGoogleBooksApi(query, startIndex);
        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return new SyncResult();
        }
        return saveBooksFromResponse(response, requireIsbn);
    }

    int resultsPerPage() {
        return RESULTS_PER_PAGE;
    }

    private GoogleBooksResponse callGoogleBooksApi(String query, int startIndex) {
        String requestUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("q", query)
                .queryParam("startIndex", startIndex)
                .queryParam("maxResults", maxResults)
                .queryParamIfPresent("langRestrict", langRestrict != null && !langRestrict.isBlank() ? java.util.Optional.of(langRestrict) : java.util.Optional.empty())
                .queryParam("printType", printType)
                .queryParam("key", apiKey)
                .toUriString();

        try {
            return restTemplate.getForObject(requestUrl, GoogleBooksResponse.class);
        } catch (Exception ex) {


            System.out.println("GOOGLE BOOKS API HATASI: " + ex.getMessage());
            throw new RuntimeException("Google Books servisine ulaşılamadı. API anahtarınızı ve internet bağlantınızı kontrol edin. (Detay: " + ex.getMessage() + ")");
        }
    }

    public static class SyncResult {
        public int added = 0;
        public int skippedDuplicate = 0;
        public int skippedNoTitle = 0;
        public int skippedNoIsbn = 0;

        public void add(SyncResult other) {
            this.added += other.added;
            this.skippedDuplicate += other.skippedDuplicate;
            this.skippedNoTitle += other.skippedNoTitle;
            this.skippedNoIsbn += other.skippedNoIsbn;
        }
    }


    
    
    private SyncResult saveBooksFromResponse(GoogleBooksResponse response, boolean requireIsbn) {
        SyncResult result = new SyncResult();

        for (var item : response.getItems()) {
            var volumeInfo = item.getVolumeInfo();
            if (volumeInfo == null) continue;

            String title = catalogHelper.normalizeText(volumeInfo.getTitle());
            if (title.isBlank()) {
                result.skippedNoTitle++;
                continue;
            }

            String realIsbn = getIsbn(volumeInfo);
            if (requireIsbn && (realIsbn == null || realIsbn.isEmpty())) {
                result.skippedNoIsbn++;
                continue;
            }

            String rawAuthor = (volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()) ? volumeInfo.getAuthors().get(0) : null;
            Author authorObj = catalogHelper.getOrCreateAuthorByFullName(rawAuthor);
            Category categoryObj = catalogHelper.getOrCreateCategoryByName(
                    (volumeInfo.getCategories() != null && !volumeInfo.getCategories().isEmpty()) ? volumeInfo.getCategories().get(0) : null
            );
            Publisher publisherObj = catalogHelper.getOrCreatePublisherByName(volumeInfo.getPublisher());

            if (bookRepository.existsByTitleIgnoreCaseAndAuthorNameIgnoreCase(title, authorObj.getName())) {
                result.skippedDuplicate++;
                continue;
            }

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(authorObj);
            book.setCategory(categoryObj);
            book.setPublisher(publisherObj);
            book.setDescription(volumeInfo.getDescription());
            book.setIsbn(realIsbn);
            book.setPageCount(volumeInfo.getPageCount());
            book.setPublicationYear(extractYear(volumeInfo.getPublishedDate()));

            double randomPrice = 150.0 + (Math.random() * 200);
            book.setPrice(BigDecimal.valueOf(randomPrice));


            
            int randomStock = 10 + (int) (Math.random() * 90);
            book.setStockQuantity(randomStock);

            if (volumeInfo.getImageLinks() != null) {
                book.setCoverImageUrl(toHttps(volumeInfo.getImageLinks().getThumbnail()));
            }

            if (realIsbn != null && !realIsbn.isEmpty()) {
                book.setSku(skuPrefix + "-" + realIsbn);
            } else {
                String uniqueCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                book.setSku(skuPrefix + "-" + uniqueCode);
            }

            bookRepository.save(book);
            result.added++;
            System.out.println("BAŞARIYLA EKLENDİ (Google) -> SKU: " + book.getSku() + " | Başlık: " + title);
        }

        return result;
    }


    private String toHttps(String url) {
        if (url == null) return null;
        return url.replaceFirst("^http://", "https://");
    }


    private Integer extractYear(String publishedDate) {
        if (publishedDate == null || publishedDate.isBlank()) return null;
        try {
            String yearPart = publishedDate.trim().substring(0, 4);
            return Integer.parseInt(yearPart);
        } catch (Exception e) {
            return null;
        }
    }

    private String getIsbn(GoogleBooksResponse.VolumeInfo info) {
        if (info.getIndustryIdentifiers() != null) {
            for (var id : info.getIndustryIdentifiers()) {
                if ("ISBN_13".equals(id.getType()) || "ISBN_10".equals(id.getType())) {
                    return id.getIdentifier();
                }
            }
        }
        return null;
    }

    @Transactional
    public List<Book> smartSearch(String query) {
        List<Book> localBooks = bookRepository.findByTitleContainingIgnoreCase(query);
        if (!localBooks.isEmpty()) {
            System.out.println("BAŞARILI: '" + query + "' araması LOKAL veritabanından getirildi.");
            return localBooks;
        }

        System.out.println("UYARI: '" + query + "' lokalde bulunamadı. Google API'ye gidilip veritabanı besleniyor...");
        fetchAndSaveBooks(query);
        return bookRepository.findByTitleContainingIgnoreCase(query);
    }
}
