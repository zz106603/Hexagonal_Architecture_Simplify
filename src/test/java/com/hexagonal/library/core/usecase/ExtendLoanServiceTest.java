package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.domain.Loan;
import com.hexagonal.library.core.domain.LoanPolicy;
import com.hexagonal.library.core.port.ClockPort;
import com.hexagonal.library.core.port.LoanRepositoryPort;
import com.hexagonal.library.core.usecase.dto.ExtendLoanCommand;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExtendLoanServiceTest {

    private final static String BOOK_ID = "B1";
    private final static String MEMBER_ID = "M1";
    private final static String LOAN_ID = "L1";
    private final static LocalDate TODAY_DATE = LocalDate.of(2025,1,1);
    private final static LocalDate DUE_DATE = LocalDate.of(2025,1,15);
    private final static LocalDate EXTEND_DATE = LocalDate.of(2025,1,29);

    LoanRepositoryPort loanRepo = mock(LoanRepositoryPort.class);
    ClockPort clock = mock(ClockPort.class);
    ExtendLoanUseCase sut = new ExtendLoanService(loanRepo, clock, new LoanPolicy(14, 1, 1));

    @Test
    void 연장_성공_dueDate가_기간만큼_미뤄진다() {
        // given
        var loan = new Loan(LOAN_ID, BOOK_ID, MEMBER_ID, TODAY_DATE, DUE_DATE);
        when(loanRepo.findActiveByBookAndMember(BOOK_ID, MEMBER_ID)).thenReturn(Optional.of(loan));
        when(clock.today()).thenReturn(TODAY_DATE);

        // when
        var res = sut.extendBook(new ExtendLoanCommand(BOOK_ID, MEMBER_ID));

        // then
        assertThat(res.loanId()).isEqualTo(LOAN_ID);
        assertThat(res.loanDate()).isEqualTo(TODAY_DATE);
        assertThat(res.dueDate()).isEqualTo(EXTEND_DATE); // 1/15 + 14
        verify(loanRepo).save(any(Loan.class));
    }

    @Test
    void 활성_대출이_없으면_연장실패() {
        when(loanRepo.findActiveByBookAndMember(BOOK_ID, MEMBER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.extendBook(new ExtendLoanCommand(BOOK_ID, MEMBER_ID)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no active loan");
    }

    @Test
    void 연체상태면_연장실패() {
        var loan = new Loan("L1", "B1", "M1",
                LocalDate.of(2025,1,1),
                LocalDate.of(2025,1,15));
        when(loanRepo.findActiveByBookAndMember("B1","M1")).thenReturn(Optional.of(loan));
        when(clock.today()).thenReturn(LocalDate.of(2025,1,16)); // overdue

        assertThatThrownBy(() -> sut.extendBook(new ExtendLoanCommand("B1","M1")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("overdue");
        verify(loanRepo, never()).save(any());
    }

    @Test
    void 최대연장횟수_초과면_연장실패() {
        var loan = new Loan(LOAN_ID, BOOK_ID, MEMBER_ID, TODAY_DATE, DUE_DATE);
        when(loanRepo.findActiveByBookAndMember(BOOK_ID, MEMBER_ID)).thenReturn(Optional.of(loan));
        when(clock.today()).thenReturn(TODAY_DATE);

        // 첫 연장 성공
        sut.extendBook(new ExtendLoanCommand(BOOK_ID, MEMBER_ID));
        assertThat(loan.getExtendCount()).isEqualTo(1);

        // 두 번째 연장 시도(정책 maxExtend=1)
        when(clock.today()).thenReturn(DUE_DATE);
        assertThatThrownBy(() -> sut.extendBook(new ExtendLoanCommand(BOOK_ID, MEMBER_ID)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("extend limit");
    }
}