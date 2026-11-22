package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.LoanRequest;
import org.example.dto.LoanResponse;
import org.example.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse issueBook(@Valid @RequestBody LoanRequest request) {
        return loanService.issueBook(request.readerId(), request.bookId());
    }

    @PostMapping("/{loanId}/return")
    public LoanResponse returnBook(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }
}
