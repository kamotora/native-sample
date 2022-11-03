package ru.kamotora.graal.spring.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class SpringMain {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringMain.class, args);
        SpringNativeApi.localesTest();
    }
}
