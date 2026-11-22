package org.example;

import org.example.dto.LoanResponse;
import org.example.entity.Book;
import org.example.entity.Loan;
import org.example.entity.Reader;
import org.example.repository.BookRepository;
import org.example.repository.LoanRepository;
import org.example.service.LoanService;
import org.example.service.ReaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LoanServiceTest {

    @Autowired
    private LoanService loanService;
    @Autowired
    private ReaderService readerService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private LoanRepository loanRepository;

    private Reader reader;

    @BeforeEach
    void setUp() {
        reader = readerService.create(new Reader("Іван Петренко", "ivan@test.com"));
    }

    @Test
    void regularReaderCanTakeOnlyTwoBooks() {
        loanService.issueBook(reader.getId(), newBook().getId());
        loanService.issueBook(reader.getId(), newBook().getId());

        assertThrows(ResponseStatusException.class,
                () -> loanService.issueBook(reader.getId(), newBook().getId()));
    }

    @Test
    void cannotIssueBorrowedBook() {
        Book book = newBook();
        loanService.issueBook(reader.getId(), book.getId());
        Reader other = readerService.create(new Reader("Олена", "olena@test.com"));

        assertThrows(ResponseStatusException.class,
                () -> loanService.issueBook(other.getId(), book.getId()));
    }

    @Test
    void readerIsReliableAfterThreeReturnsOnTime() {
        for (int i = 0; i < 3; i++) {
            addReturnedLoan(10, 2);
        }

        assertTrue(loanService.checkReliability(reader.getId()).reliable());

        for (int i = 0; i < 5; i++) {
            loanService.issueBook(reader.getId(), newBook().getId());
        }
        assertThrows(ResponseStatusException.class,
                () -> loanService.issueBook(reader.getId(), newBook().getId()));
    }

    @Test
    void lateReturnMakesReaderNotReliable() {
        addReturnedLoan(10, 2);
        addReturnedLoan(10, 2);
        addReturnedLoan(25, -5);

        assertFalse(loanService.checkReliability(reader.getId()).reliable());
    }

    @Test
    void overdueLoanMakesReaderNotReliable() {
        for (int i = 0; i < 3; i++) {
            addReturnedLoan(10, 2);
        }
        Loan overdue = new Loan(reader, newBook(), LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(6));
        loanRepository.save(overdue);

        var result = loanService.checkReliability(reader.getId());
        assertTrue(result.hasOverdueLoans());
        assertFalse(result.reliable());
    }

    @Test
    void oldReturnsAreNotCounted() {
        for (int i = 0; i < 3; i++) {
            addReturnedLoan(31, 2);
        }

        assertEquals(0, loanService.checkReliability(reader.getId()).returnedLast30Days());
    }

    @Test
    void returnBook() {
        Book book = newBook();
        LoanResponse loan = loanService.issueBook(reader.getId(), book.getId());

        LoanResponse returned = loanService.returnBook(loan.id());

        assertNotNull(returned.returnDate());
        assertTrue(loanRepository.existsById(loan.id()));
        assertTrue(loanService.getActiveLoans(reader.getId()).isEmpty());
        assertThrows(ResponseStatusException.class, () -> loanService.returnBook(loan.id()));
    }

    @Test
    void emailMustBeUnique() {
        assertThrows(ResponseStatusException.class,
                () -> readerService.create(new Reader("Інший", "IVAN@test.com")));
    }

    private Book newBook() {
        return bookRepository.save(new Book("Кобзар", "Т. Шевченко", 1840));
    }

    private void addReturnedLoan(int daysAgo, int daysBeforeDue) {
        LocalDateTime returnDate = LocalDateTime.now().minusDays(daysAgo);
        LocalDateTime dueDate = returnDate.plusDays(daysBeforeDue);
        Loan loan = new Loan(reader, newBook(), dueDate.minusDays(14), dueDate);
        loan.setReturnDate(returnDate);
        loanRepository.save(loan);
    }
}
