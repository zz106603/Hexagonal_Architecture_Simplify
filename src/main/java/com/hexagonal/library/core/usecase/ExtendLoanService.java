package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.domain.Loan;
import com.hexagonal.library.core.domain.LoanPolicy;
import com.hexagonal.library.core.port.ClockPort;
import com.hexagonal.library.core.port.LoanRepositoryPort;
import com.hexagonal.library.core.usecase.dto.ExtendLoanCommand;
import com.hexagonal.library.core.usecase.dto.LoanResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExtendLoanService implements ExtendLoanUseCase {

    private final LoanRepositoryPort loans;
    private final ClockPort clock;
    private final LoanPolicy policy;

    @Override
    public LoanResult extendBook(ExtendLoanCommand cmd) {
        Loan loan = loans.findActiveByBookAndMember(cmd.bookId(), cmd.memberId())
                .orElseThrow(() -> new IllegalStateException("no active loan"));

        loan.extend(policy.periodDays(), policy.maxExtend(), clock.today());
        loans.save(loan);

        return new LoanResult(loan.getId(), loan.getLoanDate(), loan.getDueDate());
    }
}