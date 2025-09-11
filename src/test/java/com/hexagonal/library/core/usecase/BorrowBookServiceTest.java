package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.domain.Book;
import com.hexagonal.library.core.domain.Loan;
import com.hexagonal.library.core.domain.LoanPolicy;
import com.hexagonal.library.core.domain.Member;
import com.hexagonal.library.core.port.*;
import com.hexagonal.library.core.usecase.dto.BorrowBookCommand;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BorrowBookServiceTest {

    private final static String BOOK_ID = "B1";
    private final static String BOOK_TITLE = "Effective Java";
    private final static int BOOK_TOTAL_COPIES = 3;
    private final static int BOOK_AVAILABLE_COPIES = 1;
    private final static String MEMBER_ID = "M1";
    private final static String LOAN_ID = "L1";
    private final static LocalDate TODAY_DATE = LocalDate.of(2025,1,1);
    private final static LocalDate DUE_DATE = LocalDate.of(2025,1,15);

    BookRepositoryPort bookRepo = mock(BookRepositoryPort.class);
    MemberRepositoryPort memberRepo = mock(MemberRepositoryPort.class);
    LoanRepositoryPort loanRepo = mock(LoanRepositoryPort.class);
    ClockPort clock = mock(ClockPort.class);
    IdGeneratorPort idGen = mock(IdGeneratorPort.class);

    BorrowBookUseCase sut = new BorrowBookService(
            bookRepo, memberRepo, loanRepo, clock, idGen, LoanPolicy.default14d());

    @Test
    void 대출_성공_dueDate는_today_plus_14() {
        // given
        var book = new Book(BOOK_ID, BOOK_TITLE, BOOK_TOTAL_COPIES, BOOK_AVAILABLE_COPIES);
        var member = new Member(MEMBER_ID, Member.Status.ACTIVE, 0);

        when(bookRepo.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(memberRepo.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(loanRepo.findActiveByMember(MEMBER_ID)).thenReturn(List.of());
        when(loanRepo.findActiveByBookAndMember(BOOK_ID, MEMBER_ID)).thenReturn(Optional.empty());
        when(clock.today()).thenReturn(TODAY_DATE);
        when(idGen.newId()).thenReturn(LOAN_ID);

        // when
        var res = sut.borrowBook(new BorrowBookCommand(BOOK_ID,MEMBER_ID));

        // then
        assertThat(res.loanId()).isEqualTo(LOAN_ID);
        assertThat(res.loanDate()).isEqualTo(TODAY_DATE);
        assertThat(res.dueDate()).isEqualTo(DUE_DATE);

        verify(bookRepo).save(argThat(b -> b.getAvailableCopies() == 0));
        verify(loanRepo).save(any(Loan.class));
        verify(memberRepo).save(any(Member.class));
    }

    @Test
    void 회원이_정지상태이면_대출실패() {
        var book = new Book(BOOK_ID, BOOK_ID, 3, 1);
        var member = new Member(MEMBER_ID, Member.Status.SUSPENDED, 0);

        when(bookRepo.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(memberRepo.findById(MEMBER_ID)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> sut.borrowBook(new BorrowBookCommand(BOOK_ID,MEMBER_ID)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("member suspended");
    }

    @Test
    void 회원이_대출한도_초과시_실패() {
        var book = new Book(BOOK_ID, BOOK_TITLE, BOOK_TOTAL_COPIES, BOOK_AVAILABLE_COPIES);
        var member = new Member(MEMBER_ID, Member.Status.ACTIVE, 5); // 이미 5권 대출 중

        when(bookRepo.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(memberRepo.findById(MEMBER_ID)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> sut.borrowBook(new BorrowBookCommand(BOOK_ID,MEMBER_ID)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("loan limit");
    }

    @Test
    void 책_재고가_없으면_실패() {
        var book = new Book(BOOK_ID, BOOK_TITLE, BOOK_TOTAL_COPIES, 0);
        var member = new Member(MEMBER_ID, Member.Status.ACTIVE, 0);

        when(bookRepo.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(memberRepo.findById(MEMBER_ID)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> sut.borrowBook(new BorrowBookCommand(BOOK_ID,MEMBER_ID)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no copies");
    }

    @Test
    void 이미_대출한_책을_다시_빌리면_실패() {
        var book = new Book(BOOK_ID, BOOK_TITLE, BOOK_TOTAL_COPIES, BOOK_AVAILABLE_COPIES);
        var member = new Member(MEMBER_ID, Member.Status.ACTIVE, 0);
        var existingLoan = new Loan(LOAN_ID, BOOK_ID, MEMBER_ID, LocalDate.now(), LocalDate.now().plusDays(14));

        when(bookRepo.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(memberRepo.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(loanRepo.findActiveByBookAndMember(BOOK_ID, MEMBER_ID)).thenReturn(Optional.of(existingLoan));

        assertThatThrownBy(() -> sut.borrowBook(new BorrowBookCommand(BOOK_ID,MEMBER_ID)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already borrowed");
    }
}