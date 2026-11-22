package org.example.service;

import org.example.entity.Book;
import org.example.entity.BookStatus;
import org.example.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book create(Book book) {
        book.setStatus(BookStatus.AVAILABLE);
        return bookRepository.save(book);
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }
}
