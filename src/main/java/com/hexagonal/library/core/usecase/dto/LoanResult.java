package com.hexagonal.library.core.usecase.dto;

import java.time.LocalDate;

public record LoanResult(
        String loanId, // 대출ID
        LocalDate loanDate, // 대출일
        LocalDate dueDate) // 반납일
{}
