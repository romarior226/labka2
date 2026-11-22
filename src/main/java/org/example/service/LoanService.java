package org.example.service;

import org.example.dto.LoanResponse;
import org.example.dto.ReliabilityResponse;
import org.example.entity.Book;
import org.example.entity.BookStatus;
import org.example.entity.Loan;
import org.example.entity.Reader;
import org.example.repository.BookRepository;
import org.example.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private static final int LOAN_DAYS = 14;
    private static final int REGULAR_LIMIT = 2;
    private static final int RELIABLE_LIMIT = 5;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderService readerService;

    @Transactional
    public LoanResponse issueBook(Long readerId, Long bookId) {
        Reader reader = readerService.getById(readerId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Книгу не знайдено"));

        if (book.getStatus() == BookStatus.BORROWED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Книга вже видана");
        }

        ReliabilityResponse reliability = checkReliability(readerId);
        if (reliability.activeLoans() >= reliability.maxActiveLoans()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Читач не може мати більше " + reliability.maxActiveLoans() + " книг");
        }

        LocalDateTime now = LocalDateTime.now();
        Loan loan = new Loan(reader, book, now, now.plusDays(LOAN_DAYS));
        book.setStatus(BookStatus.BORROWED);
        loanRepository.save(loan);

        return LoanResponse.from(loan);
    }

    @Transactional
    public LoanResponse returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Позику не знайдено"));

        if (loan.getReturnDate() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Книга вже повернута");
        }

        loan.setReturnDate(LocalDateTime.now());
        loan.getBook().setStatus(BookStatus.AVAILABLE);

        return LoanResponse.from(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getActiveLoans(Long readerId) {
        readerService.getById(readerId);

        List<LoanResponse> result = new ArrayList<>();
        for (Loan loan : loanRepository.findByReaderIdAndReturnDateIsNull(readerId)) {
            result.add(LoanResponse.from(loan));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public ReliabilityResponse checkReliability(Long readerId) {
        readerService.getById(readerId);

        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        List<Loan> returnedLoans = loanRepository.findByReaderIdAndReturnDateAfter(readerId, monthAgo);
        List<Loan> activeLoans = loanRepository.findByReaderIdAndReturnDateIsNull(readerId);

        boolean allOnTime = true;
        for (Loan loan : returnedLoans) {
            if (loan.getReturnDate().isAfter(loan.getDueDate())) {
                allOnTime = false;
            }
        }

        boolean hasOverdue = false;
        for (Loan loan : activeLoans) {
            if (loan.isOverdue()) {
                hasOverdue = true;
            }
        }

        boolean reliable = returnedLoans.size() >= 3 && allOnTime && !hasOverdue;
        int limit = reliable ? RELIABLE_LIMIT : REGULAR_LIMIT;

        return new ReliabilityResponse(readerId, reliable, returnedLoans.size(),
                allOnTime, hasOverdue, activeLoans.size(), limit);
    }
}
