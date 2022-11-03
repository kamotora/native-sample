package ru.kamotora.graal.spring.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Slf4j
@Configuration
public class SimpleSpringConfig {
    private static ApplicationContext applicationContext;

    public SimpleSpringConfig(ApplicationContext applicationContext) {
        SimpleSpringConfig.applicationContext = applicationContext;
        log.info("SimpleSpringConfig initialized...");
    }

    public static ApplicationContext getApplicationContext() {
        return Optional.ofNullable(applicationContext)
                .orElseThrow();
    }
}
