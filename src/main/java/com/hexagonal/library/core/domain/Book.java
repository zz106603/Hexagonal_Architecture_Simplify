package com.hexagonal.library.core.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public final class Book {
    private final String id;
    private final String title;
    private final int totalCopies; // 총 재고
    private int availableCopies; // 남은 재고

    public Book(String id, String title, int totalCopies, int availableCopies) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        if (totalCopies < 0 || availableCopies < 0 || availableCopies > totalCopies)
            throw new IllegalArgumentException("invalid copies");
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // 대출
    public void decreaseAvailable() {
        if (availableCopies <= 0) throw new IllegalStateException("no copies");
        availableCopies--;
    }

    // 반납
    public void increaseAvailable() {
        if (availableCopies >= totalCopies) throw new IllegalStateException("exceeds total");
        availableCopies++;
    }

}
