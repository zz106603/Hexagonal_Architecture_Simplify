package com.hexagonal.library.core.usecase;

import com.hexagonal.library.core.port.ClockPort;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class TodayService {

    private final ClockPort clock;

    public String getTodayIso(){
        return clock.today().format(DateTimeFormatter.ISO_DATE);
    }
}
