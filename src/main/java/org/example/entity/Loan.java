package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    public Loan() {
    }

    public Loan(Reader reader, Book book, LocalDateTime loanDate, LocalDateTime dueDate) {
        this.reader = reader;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public boolean isOverdue() {
        return returnDate == null && LocalDateTime.now().isAfter(dueDate);
    }

    public Long getId() {
        return id;
    }

    public Reader getReader() {
        return reader;
    }

    public Book getBook() {
        return book;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
}
