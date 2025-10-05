package com.library.management.lmsv1.book.controller;

import com.library.management.lmsv1.book.BookStatus;
import com.library.management.lmsv1.book.dto.BookRequestDto;
import com.library.management.lmsv1.book.dto.BookResponseDto;
import com.library.management.lmsv1.book.dto.PutCallBookRequestDto;
import com.library.management.lmsv1.book.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> create(@Valid @RequestBody BookRequestDto requestDto) {
        BookResponseDto created = bookService.create(requestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> findAll(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BookStatus status) {
        return ResponseEntity.ok(bookService.findAll(author, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody PutCallBookRequestDto requestDto) {
        return ResponseEntity.ok(bookService.update(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/published-after")
    public ResponseEntity<List<BookResponseDto>> findPublishedAfter(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return ResponseEntity.ok(bookService.findPublishedAfter(date));
    }
}

