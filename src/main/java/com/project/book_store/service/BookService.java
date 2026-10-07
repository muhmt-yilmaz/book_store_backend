package com.project.book_store.service;

import com.project.book_store.entity.concretes.Author;
import com.project.book_store.entity.concretes.Book;
import com.project.book_store.entity.concretes.Category;
import com.project.book_store.entity.concretes.Publisher;
import com.project.book_store.exception.ConflictException;
import com.project.book_store.payload.mapper.BookMapper;
import com.project.book_store.payload.request.BookRequest;
import com.project.book_store.payload.response.BookResponse;
import com.project.book_store.repository.AuthorRepository;
import com.project.book_store.repository.BookRepository;
import com.project.book_store.repository.CategoryRepository;
import com.project.book_store.repository.PublisherRepository;
import com.project.book_store.utils.Messages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;


    public Page<BookResponse> getAllBooks(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        return bookPage.map(bookMapper::toResponse);
    }


    
    public Page<BookResponse> getFilteredBooks(Long categoryId, Long authorId, Long publisherId, String search, Pageable pageable) {
        var specification = com.project.book_store.repository.BookSpecification.withFilters(categoryId, authorId, publisherId, search);
        Page<Book> bookPage = bookRepository.findAll(specification, pageable);
        return bookPage.map(bookMapper::toResponse);
    }

    public List<BookResponse> toResponseList(List<Book> books) {
        return bookMapper.toResponseList(books);
    }

    public BookResponse getBookById(Long id) {
        Book book = findBookByIdOrThrow(id);
        return bookMapper.toResponse(book);
    }

    @Transactional
    public BookResponse createBook(BookRequest bookRequest) {
        checkBookConflicts(bookRequest, null);

        Book newBook = bookMapper.toEntity(bookRequest);
        
        
        newBook.setDiscountPercentage(0);
        setBookRelations(newBook, bookRequest);
        Book savedBook = bookRepository.save(newBook);
        return bookMapper.toResponse(savedBook);
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        Book existingBook = findBookByIdOrThrow(id);

        checkBookConflicts(bookRequest, id);

        bookMapper.updateEntityFromRequest(bookRequest, existingBook);
        setBookRelations(existingBook, bookRequest);
        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.toResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = findBookByIdOrThrow(id);

        bookRepository.delete(book);
    }

    public Book findBookByIdOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(Messages.BOOK_NOT_FOUND_MESSAGE, id)));
    }

    private void setBookRelations(Book book, BookRequest request) {

        if (request.getAuthorId() != null) {
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new RuntimeException(String.format(Messages.AUTHOR_NOT_FOUND_MESSAGE, request.getAuthorId())));
            book.setAuthor(author);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException(String.format(Messages.CATEGORY_NOT_FOUND_MESSAGE, request.getCategoryId())));
            book.setCategory(category);
        }

        if (request.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new RuntimeException(String.format(Messages.PUBLISHER_NOT_FOUND_MESSAGE, request.getPublisherId())));
            book.setPublisher(publisher);
        }
    }

    private void checkBookConflicts(BookRequest request, Long currentBookId) {
        boolean isCreate = (currentBookId == null);


        
        
        
        if (request.getIsbn() != null && !request.getIsbn().isBlank()) {
            boolean isbnExists = isCreate ?
                    bookRepository.existsByIsbn(request.getIsbn()) :
                    bookRepository.existsByIsbnAndIdNot(request.getIsbn(), currentBookId);
            if (isbnExists) throw new ConflictException(Messages.BOOK_ISBN_ALREADY_EXIST_MESSAGE);
        }

        
        if (request.getSku() != null) {
            boolean skuExists = isCreate ?
                    bookRepository.existsBySku(request.getSku()) :
                    bookRepository.existsBySkuAndIdNot(request.getSku(), currentBookId);
            if (skuExists) throw new ConflictException(Messages.BOOK_SKU_ALREADY_EXIST_MESSAGE);
        }

        
        if (request.getTitle() != null && request.getAuthorId() != null) {
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new RuntimeException(String.format(Messages.AUTHOR_NOT_FOUND_MESSAGE, request.getAuthorId())));

            boolean titleExists = isCreate ?
                    bookRepository.existsByTitleIgnoreCaseAndAuthorNameIgnoreCase(request.getTitle().trim(), author.getName()) :
                    bookRepository.existsByTitleIgnoreCaseAndAuthorNameIgnoreCaseAndIdNot(request.getTitle().trim(), author.getName(), currentBookId);

            if (titleExists) throw new ConflictException(Messages.BOOK_TITLE_AUTHOR_ALREADY_EXIST_MESSAGE);
        }
    }
}
