package com.project.book_store.service;

import com.project.book_store.entity.concretes.Category;
import com.project.book_store.exception.ConflictException;
import com.project.book_store.payload.mapper.CategoryMapper;
import com.project.book_store.payload.request.CategoryRequest;
import com.project.book_store.payload.response.CategoryResponse;
import com.project.book_store.repository.CategoryRepository;
import com.project.book_store.utils.Messages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final com.project.book_store.repository.BookRepository bookRepository;

    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(categoryMapper::toResponse);
    }


    public List<CategoryResponse> getAllList() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream()
                .map(category -> {
                    CategoryResponse response = categoryMapper.toResponse(category);
                    response.setBookCount(bookRepository.countByCategoryId(category.getId()));
                    return response;
                })
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryByIdOrThrow(id);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByCategoryNameIgnoreCase(categoryRequest.getCategoryName().trim())) {
            throw new ConflictException(Messages.CATEGORY_ALREADY_EXIST_MESSAGE);
        }

        Category newCategory = categoryMapper.toEntity(categoryRequest);
        Category savedCategory = categoryRepository.save(newCategory);
        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category existingCategory = findCategoryByIdOrThrow(id);
        String newName = categoryRequest.getCategoryName().trim();

        if (!existingCategory.getCategoryName().equalsIgnoreCase(newName) &&
                categoryRepository.existsByCategoryNameIgnoreCase(newName)) {
            throw new ConflictException(Messages.CATEGORY_ALREADY_EXIST_MESSAGE);
        }

        categoryMapper.updateEntityFromRequest(categoryRequest, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category existingCategory = findCategoryByIdOrThrow(id);
        categoryRepository.delete(existingCategory);
    }

    public Category findCategoryByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(Messages.BOOK_NOT_FOUND_MESSAGE, id)));
    }
}
