package com.hexagonal.library.core.port;

import com.hexagonal.library.core.domain.Member;

import java.util.Optional;

public interface MemberRepositoryPort {
    Optional<Member> findById(String id);
    void save(Member member);
}
