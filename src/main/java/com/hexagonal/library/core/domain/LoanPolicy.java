package com.hexagonal.library.core.domain;

public record LoanPolicy(int periodDays, int maxLoansPerMember, int maxExtend) {
    public static LoanPolicy default14d() {
        // 대출 기간 14일, 최대 대출 5권, 최대 연장 1번
        return new LoanPolicy(14, 5, 1);
    }
}
