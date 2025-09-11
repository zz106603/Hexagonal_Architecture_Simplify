package com.hexagonal.library.core.port;

import java.util.Optional;
import com.hexagonal.library.core.domain.Book;

public interface BookRepositoryPort {
    Optional<Book> findById(String id);
    void save(Book book);
}
