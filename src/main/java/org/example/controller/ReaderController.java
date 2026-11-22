package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.LoanResponse;
import org.example.dto.ReliabilityResponse;
import org.example.entity.Reader;
import org.example.service.LoanService;
import org.example.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    @Autowired
    private ReaderService readerService;
    @Autowired
    private LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reader create(@Valid @RequestBody Reader reader) {
        return readerService.create(reader);
    }

    @GetMapping("/{readerId}/reliability")
    public ReliabilityResponse getReliability(@PathVariable Long readerId) {
        return loanService.checkReliability(readerId);
    }

    @GetMapping("/{readerId}/loans/active")
    public List<LoanResponse> getActiveLoans(@PathVariable Long readerId) {
        return loanService.getActiveLoans(readerId);
    }
}
