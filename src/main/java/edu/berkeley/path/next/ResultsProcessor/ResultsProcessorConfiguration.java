package edu.berkeley.path.next.ResultsProcessor;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;

// use a configuration object instead of

@Configuration
@EnableAsync
public class ResultsProcessorConfiguration {

    @Bean
    public LinkCalculatorResult linkCalculatorResult(){
        return new LinkCalculatorResult();
    }

    @Bean
    public LinkCalculator linkCalculatorManager(){
        return new LinkCalculator();
    }


}


