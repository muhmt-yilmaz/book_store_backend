package com.project.book_store.repository;

import com.project.book_store.entity.concretes.Book;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class BookSpecification {

    public static Specification<Book> withFilters(Long categoryId, Long authorId, Long publisherId, String search) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                Join<Object, Object> categoryJoin = root.join("category", JoinType.INNER);
                predicates.add(cb.equal(categoryJoin.get("id"), categoryId));
            }
            if (authorId != null) {
                Join<Object, Object> authorJoin = root.join("author", JoinType.INNER);
                predicates.add(cb.equal(authorJoin.get("id"), authorId));
            }
            if (publisherId != null) {
                Join<Object, Object> publisherJoin = root.join("publisher", JoinType.INNER);
                predicates.add(cb.equal(publisherJoin.get("id"), publisherId));
            }
            if (search != null && !search.isBlank()) {
                Join<Object, Object> authorJoinForSearch = root.join("author", JoinType.LEFT);
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likePattern),
                        cb.like(cb.lower(authorJoinForSearch.get("name")), likePattern),
                        cb.like(cb.lower(authorJoinForSearch.get("surname")), likePattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
