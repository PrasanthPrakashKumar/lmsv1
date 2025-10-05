package com.library.management.lmsv1.book.dto;

import com.library.management.lmsv1.book.BookStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class BookRequestDto {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Author is required")
    private String author;
    private String isbn; // optional but unique if present
    private LocalDate publishedDate;
    private BookStatus status;

    public BookRequestDto() {}

    public BookRequestDto(String title, String author, String isbn, LocalDate publishedDate, BookStatus status) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
        this.status = status;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
}

