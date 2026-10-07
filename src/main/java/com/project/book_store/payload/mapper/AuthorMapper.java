package com.project.book_store.payload.mapper;

import com.project.book_store.entity.concretes.Author;
import com.project.book_store.payload.request.AuthorRequest;
import com.project.book_store.payload.response.AuthorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    Author toEntity(AuthorRequest request);

    AuthorResponse toResponse(Author author);

    List<AuthorResponse> toResponseList(List<Author> authors);

    void updateEntityFromRequest(AuthorRequest request, @MappingTarget Author author);
}
