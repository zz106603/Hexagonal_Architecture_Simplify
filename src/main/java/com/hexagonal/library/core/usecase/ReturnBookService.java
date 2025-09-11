package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.domain.Book;
import com.hexagonal.library.core.domain.Loan;
import com.hexagonal.library.core.domain.Member;
import com.hexagonal.library.core.port.BookRepositoryPort;
import com.hexagonal.library.core.port.LoanRepositoryPort;
import com.hexagonal.library.core.port.MemberRepositoryPort;
import com.hexagonal.library.core.usecase.dto.ReturnBookCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReturnBookService implements ReturnBookUseCase {

    private final BookRepositoryPort books;
    private final LoanRepositoryPort loans;
    private final MemberRepositoryPort members;

    @Override
    public void returnBook(ReturnBookCommand cmd) {
        // 1) 활성 대출 조회
        Loan loan = loans.findActiveByBookAndMember(cmd.bookId(), cmd.memberId())
                .orElseThrow(() -> new IllegalStateException("no active loan"));

        // 2) 대출 상태 → RETURNED
        loan.markReturned();
        loans.save(loan);

        // 3) 책 재고 +1
        Book book = books.findById(cmd.bookId()).orElseThrow();
        book.increaseAvailable();
        books.save(book);

        // 4) 회원 대출 카운트 -1
        Member member = members.findById(cmd.memberId()).orElseThrow();
        member.decLoan();
        members.save(member);
    }
}
