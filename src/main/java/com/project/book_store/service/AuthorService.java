package com.project.book_store.service;

import com.project.book_store.entity.concretes.Author;
import com.project.book_store.entity.concretes.Book;
import com.project.book_store.payload.mapper.AuthorMapper;
import com.project.book_store.payload.request.AuthorRequest;
import com.project.book_store.payload.response.AuthorBookResponse;
import com.project.book_store.payload.response.AuthorDetailResponse;
import com.project.book_store.payload.response.AuthorResponse;
import com.project.book_store.repository.AuthorRepository;
import com.project.book_store.repository.BookRepository;
import com.project.book_store.utils.Messages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;


    public Page<AuthorResponse> getAllAuthors(Pageable pageable) {

        Page<Author> authorPage = authorRepository.findAll(pageable);
        return authorPage.map(authorMapper::toResponse);
    }

    public List<AuthorResponse> getAllList() {
        List<Author> authors = authorRepository.findAll();
        return authorMapper.toResponseList(authors);
    }

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest authorRequest) {
        Author newAuthor = authorMapper.toEntity(authorRequest);
        Author savedAuthor = authorRepository.save(newAuthor);
        return authorMapper.toResponse(savedAuthor);
    }

    public AuthorResponse getAuthorById(Long id) {
        Author author = findAuthorByIdOrThrow(id);
        return authorMapper.toResponse(author);
    }

    
    public AuthorDetailResponse getAuthorDetailWithBooks(Long id) {
        Author author = findAuthorByIdOrThrow(id);
        List<Book> books = bookRepository.findByAuthorId(id);

        List<AuthorBookResponse> bookResponses = books.stream()
                .map(book -> AuthorBookResponse.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .isbn(book.getIsbn())
                        .coverImageUrl(book.getCoverImageUrl())
                        .price(book.getPrice())
                        .discountPercentage(book.getDiscountPercentage())
                        .categoryName(book.getCategory() != null ? book.getCategory().getCategoryName() : null)
                        .build())
                .toList();

        return AuthorDetailResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .surname(author.getSurname())
                .biography(author.getBiography())
                .nationality(author.getNationality())
                .birthDate(author.getBirthDate())
                .websiteUrl(author.getWebsiteUrl())
                .books(bookResponses)
                .build();
    }

    @Transactional
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author existingAuthor = findAuthorByIdOrThrow(id);

        authorMapper.updateEntityFromRequest(request, existingAuthor);

        Author updatedAuthor = authorRepository.save(existingAuthor);
        return authorMapper.toResponse(updatedAuthor);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        Author existingAuthor = findAuthorByIdOrThrow(id);

        authorRepository.delete(existingAuthor);
    }

    private Author findAuthorByIdOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(Messages.AUTHOR_NOT_FOUND_MESSAGE, id)));
    }

}
