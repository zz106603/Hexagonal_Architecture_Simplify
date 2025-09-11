package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.usecase.dto.ExtendLoanCommand;
import com.hexagonal.library.core.usecase.dto.LoanResult;

public interface ExtendLoanUseCase {
    LoanResult extendBook(ExtendLoanCommand cmd);
}