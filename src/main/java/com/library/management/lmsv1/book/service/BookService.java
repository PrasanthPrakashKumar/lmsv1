package com.library.management.lmsv1.book.service;

import com.library.management.lmsv1.book.BookStatus;
import com.library.management.lmsv1.book.dto.BookRequestDto;
import com.library.management.lmsv1.book.dto.BookResponseDto;
import com.library.management.lmsv1.book.dto.PutCallBookRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface BookService {
    BookResponseDto create(BookRequestDto requestDto);
    List<BookResponseDto> findAll(String author, BookStatus status);
    BookResponseDto findById(Long id);
    BookResponseDto update(Long id, PutCallBookRequestDto requestDto);
    void delete(Long id);
    List<BookResponseDto> findPublishedAfter(LocalDate date);


}

