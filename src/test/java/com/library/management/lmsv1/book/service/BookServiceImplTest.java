package com.library.management.lmsv1.book.service;

import com.library.management.lmsv1.book.Book;
import com.library.management.lmsv1.book.BookStatus;
import com.library.management.lmsv1.book.dto.BookRequestDto;
import com.library.management.lmsv1.book.dto.BookResponseDto;
import com.library.management.lmsv1.book.dto.PutCallBookRequestDto;
import com.library.management.lmsv1.book.exception.DuplicateResourceException;
import com.library.management.lmsv1.book.exception.ResourceNotFoundException;
import com.library.management.lmsv1.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book existing;

    @BeforeEach
    void setup() {
        existing = new Book("Existing", "Author", "ISBN-1", LocalDate.of(2020,1,1), BookStatus.AVAILABLE);
        existing.setId(1L);
    }

    @Test
    @DisplayName("Create book successfully")
    void createSuccess() {
        BookRequestDto req = new BookRequestDto("Title","Auth","ISBN-NEW", LocalDate.now(), BookStatus.AVAILABLE);
        when(bookRepository.existsByIsbn("ISBN-NEW")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            b.setId(10L);
            return b;
        });
        BookResponseDto resp = bookService.create(req);
        assertNotNull(resp.getId());
        assertEquals("Title", resp.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Create book duplicate ISBN")
    void createDuplicateIsbn() {
        BookRequestDto req = new BookRequestDto("Title","Auth","ISBN-1", LocalDate.now(), BookStatus.AVAILABLE);
        when(bookRepository.existsByIsbn("ISBN-1")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> bookService.create(req));
    }

    @Test
    @DisplayName("Find by id not found")
    void findByIdNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.findById(99L));
    }

    @Test
    @DisplayName("Update duplicate ISBN")
    void updateDuplicateIsbn() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByIsbn("OTHER")).thenReturn(true);
        PutCallBookRequestDto req = new PutCallBookRequestDto("NewT","NewA","OTHER", LocalDate.now(), BookStatus.BORROWED);
        assertThrows(DuplicateResourceException.class, () -> bookService.update(1L, req));
    }

    @Test
    @DisplayName("Find published after with streams")
    void findPublishedAfter() {
        Book b1 = existing; // 2020-01-01
        Book b2 = new Book("Older","A","OLD", LocalDate.of(2010,1,1), BookStatus.AVAILABLE); b2.setId(2L);
        Book b3 = new Book("Newer","A","NEW", LocalDate.of(2024,1,1), BookStatus.BORROWED); b3.setId(3L);
        when(bookRepository.findAll()).thenReturn(List.of(b1,b2,b3));
        List<BookResponseDto> result = bookService.findPublishedAfter(LocalDate.of(2021,1,1));
        assertEquals(1, result.size());
        assertEquals("Newer", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Find by id success")
    void findByIdSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        BookResponseDto dto = bookService.findById(1L);
        assertEquals("Existing", dto.getTitle());
        assertEquals(1L, dto.getId());
    }

    @Test
    @DisplayName("Update partial fields success")
    void updatePartialFieldsSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PutCallBookRequestDto partial = new PutCallBookRequestDto();
        partial.setTitle("Updated Title"); // only title changed
        BookResponseDto updated = bookService.update(1L, partial);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Author", updated.getAuthor());
    }

    @Test
    @DisplayName("Delete not found")
    void deleteNotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(99L));
    }
}
