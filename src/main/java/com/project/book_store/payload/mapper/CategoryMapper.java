package com.project.book_store.payload.mapper;

import com.project.book_store.entity.concretes.Category;
import com.project.book_store.payload.request.CategoryRequest;
import com.project.book_store.payload.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest categoryRequest);

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);

    void updateEntityFromRequest(CategoryRequest categoryRequest,@MappingTarget Category category);

}
