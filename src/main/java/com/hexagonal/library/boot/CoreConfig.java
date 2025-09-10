package com.hexagonal.library.boot;

import com.hexagonal.library.adapter.out.time.SystemClockAdapter;
import com.hexagonal.library.core.port.ClockPort;
import com.hexagonal.library.core.usecase.TodayService;
import com.hexagonal.library.web.TodayController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

    @Bean
    public ClockPort clockPort(){
        return new SystemClockAdapter();
    }

    @Bean
    public TodayService todayService(ClockPort clockPort){
        return new TodayService(clockPort);
    }
}
