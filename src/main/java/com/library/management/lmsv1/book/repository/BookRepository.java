package com.library.management.lmsv1.book.repository;

import com.library.management.lmsv1.book.Book;
import com.library.management.lmsv1.book.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByAuthorIgnoreCase(String author);
    List<Book> findByStatus(BookStatus status);
}

