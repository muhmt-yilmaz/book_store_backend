package com.project.book_store.entity.concretes;

import com.project.book_store.entity.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "publishers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Publisher extends BaseEntity {

    private String name;

    @Column(length = 1000)
    private String description;

    private String website;

    private String contactEmail;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books;
}