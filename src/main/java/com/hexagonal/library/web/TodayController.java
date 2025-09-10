package com.hexagonal.library.web;

import com.hexagonal.library.core.usecase.TodayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TodayController {

    private final TodayService todayService;

    @GetMapping("/today")
    public String today(){
        return todayService.getTodayIso();
    }
}
