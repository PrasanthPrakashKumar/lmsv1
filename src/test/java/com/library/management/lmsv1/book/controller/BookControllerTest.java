package com.library.management.lmsv1.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.lmsv1.book.BookStatus;
import com.library.management.lmsv1.book.dto.BookRequestDto;
import com.library.management.lmsv1.book.dto.BookResponseDto;
import com.library.management.lmsv1.book.dto.PutCallBookRequestDto;
import com.library.management.lmsv1.book.exception.ResourceNotFoundException;
import com.library.management.lmsv1.book.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private BookResponseDto sample(Long id) {
        return new BookResponseDto(id, "Title"+id, "Author", "ISBN"+id, LocalDate.of(2020,1,1), BookStatus.AVAILABLE);
    }

    @Test
    @DisplayName("POST create returns 201 and Location header")
    void createBook201() throws Exception {
        BookRequestDto req = new BookRequestDto("New Title", "New Author", "ISBN-100", LocalDate.of(2022,1,1), BookStatus.AVAILABLE);
        BookResponseDto created = new BookResponseDto(100L, req.getTitle(), req.getAuthor(), req.getIsbn(), req.getPublishedDate(), req.getStatus());
        given(bookService.create(any(BookRequestDto.class))).willReturn(created);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/books/100")))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    @DisplayName("POST invalid returns 400 with validation errors")
    void createInvalid400() throws Exception {
        BookRequestDto req = new BookRequestDto(); // missing required fields
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }

    @Test
    @DisplayName("GET /{id} 200 OK")
    void getById200() throws Exception {
        given(bookService.findById(5L)).willReturn(sample(5L));
        mockMvc.perform(get("/api/v1/books/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("GET /{id} not found 404")
    void getById404() throws Exception {
        given(bookService.findById(999L)).willThrow(new ResourceNotFoundException("Book not found with id=999"));
        mockMvc.perform(get("/api/v1/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET list with filters")
    void getListFiltered() throws Exception {
        given(bookService.findAll("Author", BookStatus.AVAILABLE)).willReturn(List.of(sample(1L), sample(2L)));
        mockMvc.perform(get("/api/v1/books").param("author","Author").param("status","AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("PUT update returns 200")
    void update200() throws Exception {
        BookRequestDto req = new BookRequestDto("Upd", "Auth", "ISBN1", LocalDate.of(2023,1,1), BookStatus.BORROWED);
        BookResponseDto updated = new BookResponseDto(1L, req.getTitle(), req.getAuthor(), req.getIsbn(), req.getPublishedDate(), req.getStatus());
        given(bookService.update(eq(1L), any(PutCallBookRequestDto.class))).willReturn(updated);
        mockMvc.perform(put("/api/v1/books/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BORROWED"));
    }

    @Test
    @DisplayName("DELETE returns 204")
    void delete204() throws Exception {
        mockMvc.perform(delete("/api/v1/books/3"))
                .andExpect(status().isNoContent());
        Mockito.verify(bookService).delete(3L);
    }

    @Test
    @DisplayName("DELETE not found 404")
    void deleteNotFound404() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Book not found with id=999"))
                .when(bookService).delete(999L);
        mockMvc.perform(delete("/api/v1/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET published-after uses date param")
    void publishedAfter() throws Exception {
        given(bookService.findPublishedAfter(LocalDate.of(2022,1,1))).willReturn(List.of(sample(10L)));
        mockMvc.perform(get("/api/v1/books/published-after").param("date", "2022-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }
}
