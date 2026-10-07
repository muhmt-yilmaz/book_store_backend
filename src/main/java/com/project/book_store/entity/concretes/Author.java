package com.project.book_store.entity.concretes;

import com.project.book_store.entity.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Author extends BaseEntity {

    private String name;

    private String surname;

    @Column(columnDefinition = "TEXT")
    private String biography;

    private String nationality;

    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String websiteUrl;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books;
}