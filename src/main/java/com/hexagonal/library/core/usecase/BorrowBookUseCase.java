package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.usecase.dto.BorrowBookCommand;
import com.hexagonal.library.core.usecase.dto.LoanResult;

public interface BorrowBookUseCase {
    // BookId, MemberId를 파라미터로 넘기고
    // LoanResult로 loanId, loanDate, dueDate를 응답받는다.
    LoanResult borrowBook(BorrowBookCommand cmd);
}
