package com.library.management.lmsv1.common;

import com.library.management.lmsv1.book.Book;
import com.library.management.lmsv1.book.BookStatus;
import com.library.management.lmsv1.book.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;

    public DataSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            List<Book> books = List.of(
                    new Book("Effective Java", "Joshua Bloch", "9780134685991", LocalDate.of(2018,1,6), BookStatus.AVAILABLE),
                    new Book("Clean Code", "Robert C. Martin", "9780132350884", LocalDate.of(2008,8,1), BookStatus.AVAILABLE),
                    new Book("Domain-Driven Design", "Eric Evans", "9780321125217", LocalDate.of(2003,8,30), BookStatus.BORROWED),
                    new Book("Refactoring", "Martin Fowler", "9780201485677", LocalDate.of(1999,7,8), BookStatus.AVAILABLE),
                    new Book("Test-Driven Development", "Kent Beck", "9780321146533", LocalDate.of(2002,11,8), BookStatus.BORROWED)
            );
            bookRepository.saveAll(books);
        }
    }
}

