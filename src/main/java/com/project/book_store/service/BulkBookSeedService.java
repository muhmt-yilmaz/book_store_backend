package com.project.book_store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BulkBookSeedService {

    private final GoogleBooksService googleBooksService;
    private final OpenLibraryService openLibraryService;


    


    private static final int MAX_TOTAL_API_CALLS = 20;

    public ResponseEntity<String> bulkFetchNewBooks(int targetCount) {
        GoogleBooksService.SyncResult total = new GoogleBooksService.SyncResult();
        int apiCallsMade = 0;

        int startSubjectIndex = new java.util.Random().nextInt(GoogleBooksService.BULK_FETCH_SUBJECTS.length);


        int olSubjectIndex = new java.util.Random().nextInt(OpenLibraryService.SUBJECTS.length);
        for (int i = 0; i < OpenLibraryService.SUBJECTS.length && total.added < targetCount && apiCallsMade < MAX_TOTAL_API_CALLS; i++) {
            String subject = OpenLibraryService.SUBJECTS[(olSubjectIndex + i) % OpenLibraryService.SUBJECTS.length];

            GoogleBooksService.SyncResult pageResult = openLibraryService.fetchOnePage(subject, 0);
            apiCallsMade++;
            total.add(pageResult);

            sleepPolitely(); 
        }

        
        outer:
        for (int i = 0; i < GoogleBooksService.BULK_FETCH_SUBJECTS.length && total.added < targetCount; i++) {
            String subject = GoogleBooksService.BULK_FETCH_SUBJECTS[(startSubjectIndex + i) % GoogleBooksService.BULK_FETCH_SUBJECTS.length];

            for (int startIndex = 0; startIndex < 120; startIndex += 40) {
                if (total.added >= targetCount || apiCallsMade >= MAX_TOTAL_API_CALLS) break outer;

                GoogleBooksService.SyncResult pageResult;
                try {
                    pageResult = googleBooksService.fetchOnePage(subject, startIndex, true);
                    apiCallsMade++;
                } catch (Exception ex) {
                    
                    
                    System.out.println("TOPLU GETİRME - Google '" + subject + "' için hata, atlanıyor: " + ex.getMessage());
                    break;
                }

                if (pageResult.added == 0 && pageResult.skippedDuplicate == 0 && pageResult.skippedNoIsbn == 0 && pageResult.skippedNoTitle == 0) {
                    break; 
                }
                total.add(pageResult);
            }
        }

        String reachedTarget = total.added >= targetCount ? "" :
                " (Hedeflenen " + targetCount + " sayısına bu tetiklemede ulaşılamadı - " +
                "kaynakların kota/hız sınırları nedeniyle bazen birden fazla tıklama gerekebilir; " +
                "butona tekrar basarak devam edebilirsiniz.)";

        String message = String.format(
                "Toplu kitap getirme tamamlandı: %d yeni kitap eklendi (%d zaten mevcuttu, %d gerçek yayın/kapak görseli olmadığı için atlandı).%s",
                total.added, total.skippedDuplicate, total.skippedNoIsbn, reachedTarget
        );
        System.out.println(message);
        return ResponseEntity.ok(message);
    }

    private void sleepPolitely() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
