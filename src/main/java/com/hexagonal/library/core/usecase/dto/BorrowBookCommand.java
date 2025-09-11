package com.hexagonal.library.core.usecase.dto;

// 어떤 책(bookId)을, 어떤 회원(memberId)이 빌리려 한다.
public record BorrowBookCommand(String bookId, String memberId) {}
