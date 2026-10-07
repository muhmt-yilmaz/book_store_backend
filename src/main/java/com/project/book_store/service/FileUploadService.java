package com.project.book_store.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {


    


    
    @Value("${app.upload.dir:${user.home}/bookstore-uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            
            System.out.println("========================================================");
            System.out.println("KAPAK GÖRSELLERİ ŞU KLASÖRE KAYDEDİLİYOR: " + uploadPath.toAbsolutePath());
            System.out.println("========================================================");
        } catch (IOException e) {
            System.out.println("UYARI: Yükleme klasörü oluşturulamadı: " + e.getMessage());
        }
    }

    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Lütfen yüklenecek bir dosya seçin.");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            
            return "/uploads/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Dosya yüklenirken bir hata oluştu: " + e.getMessage());
        }
    }
}