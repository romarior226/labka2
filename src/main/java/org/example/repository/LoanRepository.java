package org.example.repository;

import org.example.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByReaderIdAndReturnDateIsNull(Long readerId);

    List<Loan> findByReaderIdAndReturnDateAfter(Long readerId, LocalDateTime date);
}
