package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.domain.Book;
import com.hexagonal.library.core.domain.Loan;
import com.hexagonal.library.core.domain.LoanPolicy;
import com.hexagonal.library.core.domain.Member;
import com.hexagonal.library.core.port.*;
import com.hexagonal.library.core.usecase.dto.BorrowBookCommand;
import com.hexagonal.library.core.usecase.dto.LoanResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class BorrowBookService implements BorrowBookUseCase {

    private final BookRepositoryPort books;
    private final MemberRepositoryPort members;
    private final LoanRepositoryPort loans;
    private final ClockPort clock;
    private final IdGeneratorPort ids;
    private final LoanPolicy policy;

    @Override
    public LoanResult borrowBook(BorrowBookCommand cmd) {
        Member member = members.findById(cmd.memberId()).orElseThrow();
        Book book = books.findById(cmd.bookId()).orElseThrow();

        if (!member.isActive()) throw new IllegalStateException("member suspended");
        if (member.getActiveLoanCount() >= policy.maxLoansPerMember())
            throw new IllegalStateException("loan limit");
        if (book.getAvailableCopies() <= 0) throw new IllegalStateException("no copies");
        if (loans.findActiveByBookAndMember(book.getId(), member.getId()).isPresent())
            throw new IllegalStateException("already borrowed");

        book.decreaseAvailable(); // 책 재고
        books.save(book);

        LocalDate today = clock.today();
        Loan loan = new Loan(ids.newId(), book.getId(), member.getId(), today, today.plusDays(policy.periodDays()));
        loans.save(loan);

        member.incLoan();
        members.save(member);

        return new LoanResult(loan.getId(), loan.getLoanDate(), loan.getDueDate());
    }
}
