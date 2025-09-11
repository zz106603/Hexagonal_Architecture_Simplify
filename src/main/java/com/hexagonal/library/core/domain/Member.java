package com.hexagonal.library.core.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public final class Member {
    public enum Status { ACTIVE, SUSPENDED }

    private final String id;
    private final Status status; // 회원 활성화 상태
    private int activeLoanCount; // 가능한 대출 개수

    public Member(String id, Status status, int activeLoanCount) {
        this.id = Objects.requireNonNull(id);
        this.status = Objects.requireNonNull(status);
        this.activeLoanCount = Math.max(0, activeLoanCount);
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public void incLoan() {
        activeLoanCount++;
    }

    public void decLoan() {
        if (activeLoanCount > 0) {
            activeLoanCount--;
        }
    }
}
