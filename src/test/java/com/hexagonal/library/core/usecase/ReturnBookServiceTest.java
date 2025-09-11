package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.domain.Book;
import com.hexagonal.library.core.domain.Loan;
import com.hexagonal.library.core.domain.Member;
import com.hexagonal.library.core.port.BookRepositoryPort;
import com.hexagonal.library.core.port.LoanRepositoryPort;
import com.hexagonal.library.core.port.MemberRepositoryPort;
import com.hexagonal.library.core.usecase.dto.ReturnBookCommand;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReturnBookServiceTest {

    private final static String BOOK_ID = "B1";
    private final static String BOOK_TITLE = "Effective Java";
    private final static int BOOK_TOTAL_COPIES = 3;
    private final static int BOOK_AVAILABLE_COPIES = 1;
    private final static String MEMBER_ID = "M1";
    private final static String LOAN_ID = "L1";
    private final static LocalDate TODAY_DATE = LocalDate.of(2025,1,1);
    private final static LocalDate DUE_DATE = LocalDate.of(2025,1,15);

    BookRepositoryPort bookRepo = mock(BookRepositoryPort.class);
    LoanRepositoryPort loanRepo = mock(LoanRepositoryPort.class);
    MemberRepositoryPort memberRepo = mock(MemberRepositoryPort.class);

    ReturnBookUseCase sut = new ReturnBookService(bookRepo, loanRepo, memberRepo);

    @Test
    void 반납_성공_재고복구와_회원카운트감소() {
        // given
        var book = new Book(BOOK_ID, BOOK_TITLE, BOOK_TOTAL_COPIES, 0);
        var member = new Member(MEMBER_ID, Member.Status.ACTIVE, 1);
        var loan = new Loan(LOAN_ID, BOOK_ID, MEMBER_ID, TODAY_DATE, DUE_DATE);

        when(loanRepo.findActiveByBookAndMember(BOOK_ID, MEMBER_ID)).thenReturn(Optional.of(loan));
        when(bookRepo.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(memberRepo.findById(MEMBER_ID)).thenReturn(Optional.of(member));

        // when
        sut.returnBook(new ReturnBookCommand(BOOK_ID, MEMBER_ID));

        // then
        verify(loanRepo).save(argThat(l -> l.getId().equals(LOAN_ID))); // RETURNED 상태 저장되었는지까지 보려면 Loan에 getter/상태 확인 추가
        verify(bookRepo).save(argThat(b -> b.getAvailableCopies() == 1));
        verify(memberRepo).save(argThat(m -> m.getActiveLoanCount() == 0));
    }

    @Test
    void 활성대출이_없으면_반납실패() {
        when(loanRepo.findActiveByBookAndMember(BOOK_ID,MEMBER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.returnBook(new ReturnBookCommand(BOOK_ID, MEMBER_ID)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no active loan");
    }
}