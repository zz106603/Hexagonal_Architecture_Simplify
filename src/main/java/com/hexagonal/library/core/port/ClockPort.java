package com.hexagonal.library.core.port;

import java.time.LocalDate;

public interface ClockPort {
    LocalDate today();
}
