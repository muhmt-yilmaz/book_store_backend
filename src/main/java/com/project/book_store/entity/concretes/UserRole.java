package com.project.book_store.entity.concretes;

import com.project.book_store.entity.abstracts.BaseEntity;
import com.project.book_store.entity.enums.RoleType;
import lombok.*;

import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserRole extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleType roleType;
}