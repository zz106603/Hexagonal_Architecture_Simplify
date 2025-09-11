package com.hexagonal.library.core.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public final class Loan {
    public enum Status { ACTIVE, RETURNED }

    private final String id;
    private final String bookId;
    private final String memberId;
    private int extendCount;
    private Status status; // 대출 상태
    private final LocalDate loanDate; // 대출 날짜
    private LocalDate dueDate; // 반납 기한

    public Loan(String id, String bookId, String memberId, LocalDate loanDate, LocalDate dueDate) {
        this.id = Objects.requireNonNull(id);
        this.bookId = Objects.requireNonNull(bookId);
        this.memberId = Objects.requireNonNull(memberId);
        this.loanDate = Objects.requireNonNull(loanDate);
        this.dueDate = Objects.requireNonNull(dueDate);
        this.status = Status.ACTIVE;
        this.extendCount = 0;
    }

    // 반납
    public void markReturned() {
        if (status == Status.RETURNED) throw new IllegalStateException("already returned");
        status = Status.RETURNED;
    }

    //
    public void extend(int days, int maxExtend, LocalDate today) {
        if (status != Status.ACTIVE) throw new IllegalStateException("not active");
        if (today.isAfter(dueDate)) throw new IllegalStateException("overdue");
        if (extendCount >= maxExtend) throw new IllegalStateException("extend limit");
        dueDate = dueDate.plusDays(days);
        extendCount++;
    }
}
