package org.example.dto;

import org.example.entity.Loan;

import java.time.LocalDateTime;

public record LoanResponse(Long id,
                           Long readerId,
                           Long bookId,
                           String bookTitle,
                           LocalDateTime loanDate,
                           LocalDateTime dueDate,
                           LocalDateTime returnDate,
                           boolean overdue) {

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getReader().getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.isOverdue()
        );
    }
}
