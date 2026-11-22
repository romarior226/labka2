package org.example.controller;

import jakarta.validation.Valid;
import org.example.entity.Book;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@Valid @RequestBody Book book) {
        return bookService.create(book);
    }

    @GetMapping
    public List<Book> getAll() {
        return bookService.getAll();
    }
}
