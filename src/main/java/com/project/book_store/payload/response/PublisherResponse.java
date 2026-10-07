package com.project.book_store.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherResponse {

    private Long id;
    private String name;
    private String description;
    private String website;
    private String contactEmail;
}