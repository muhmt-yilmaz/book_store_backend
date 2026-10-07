package com.project.book_store.payload.mapper;

import com.project.book_store.entity.concretes.Book;
import com.project.book_store.payload.request.BookRequest;
import com.project.book_store.payload.response.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookRequest request);


    
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "publisherId", source = "publisher.id")
    @Mapping(target = "publisherName", source = "publisher.name")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.categoryName")
    BookResponse toResponse(Book book);

    List<BookResponse> toResponseList(List<Book> books);

    
    void updateEntityFromRequest(BookRequest request, @MappingTarget Book book);
}
