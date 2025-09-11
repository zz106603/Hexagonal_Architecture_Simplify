package com.hexagonal.library.core.port;

import com.hexagonal.library.core.domain.Loan;

import java.util.List;
import java.util.Optional;
public interface LoanRepositoryPort {
    Optional<Loan> findActiveByBookAndMember(String bookId, String memberId);
    List<Loan> findActiveByMember(String memberId);
    void save(Loan loan);
}
