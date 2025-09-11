package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.usecase.dto.ReturnBookCommand;

public interface ReturnBookUseCase {
    void returnBook(ReturnBookCommand cmd);
}