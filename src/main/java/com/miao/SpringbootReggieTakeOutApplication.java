package com.miao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching //开启注解缓存功能
public class SpringbootReggieTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootReggieTakeOutApplication.class, args);
        log.info("Application started");
    }

}
