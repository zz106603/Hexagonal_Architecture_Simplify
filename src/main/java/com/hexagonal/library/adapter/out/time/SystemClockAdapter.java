package com.hexagonal.library.adapter.out.time;

import com.hexagonal.library.core.port.ClockPort;

import java.time.LocalDate;

public class SystemClockAdapter implements ClockPort {

    @Override
    public LocalDate today(){
        return LocalDate.now();
    }
}
