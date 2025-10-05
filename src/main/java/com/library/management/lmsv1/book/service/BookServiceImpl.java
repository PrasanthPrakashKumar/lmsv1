package com.library.management.lmsv1.book.service;

import com.library.management.lmsv1.book.Book;
import com.library.management.lmsv1.book.BookStatus;
import com.library.management.lmsv1.book.dto.BookRequestDto;
import com.library.management.lmsv1.book.dto.BookResponseDto;
import com.library.management.lmsv1.book.dto.PutCallBookRequestDto;
import com.library.management.lmsv1.book.exception.DuplicateResourceException;
import com.library.management.lmsv1.book.exception.ResourceNotFoundException;
import com.library.management.lmsv1.book.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookResponseDto create(BookRequestDto requestDto) {
        if (requestDto.getIsbn() != null && bookRepository.existsByIsbn(requestDto.getIsbn())) {
            throw new DuplicateResourceException("ISBN already exists");
        }
        Book book = new Book(
                requestDto.getTitle(),
                requestDto.getAuthor(),
                requestDto.getIsbn(),
                requestDto.getPublishedDate(),
                requestDto.getStatus() == null ? BookStatus.AVAILABLE : requestDto.getStatus()
        );
        return mapToResponse(bookRepository.save(book));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> findAll(String author, BookStatus status) {
        return bookRepository.findAll().stream()
                .filter(b -> author == null || b.getAuthor().equalsIgnoreCase(author))
                .filter(b -> status == null || b.getStatus() == status)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto findById(Long id) {
        return bookRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id=" + id));
    }

    @Override
    public BookResponseDto update(Long id, PutCallBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id=" + id));
        if (requestDto.getIsbn() != null && !Objects.equals(requestDto.getIsbn(), book.getIsbn())
                && bookRepository.existsByIsbn(requestDto.getIsbn())) {
            throw new DuplicateResourceException("ISBN already exists");
        }
        System.out.println("Update book API called with id =" + id);
        System.out.println("Details of fetched book are " + book.getAuthor() + " " + book.getId() + " " + book.getTitle());
        if (requestDto.getTitle() != null) book.setTitle(requestDto.getTitle());
        if (requestDto.getAuthor() != null) book.setAuthor(requestDto.getAuthor());
        if (requestDto.getIsbn() != null) book.setIsbn(requestDto.getIsbn());
        if (requestDto.getPublishedDate() != null) book.setPublishedDate(requestDto.getPublishedDate());
        if (requestDto.getStatus() != null) book.setStatus(requestDto.getStatus());
        return mapToResponse(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id=" + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> findPublishedAfter(LocalDate date) {
        return bookRepository.findAll().stream()
                .filter(b -> b.getPublishedDate() != null && b.getPublishedDate().isAfter(date))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookResponseDto mapToResponse(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublishedDate(),
                book.getStatus()
        );
    }
}

