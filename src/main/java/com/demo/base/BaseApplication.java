package com.demo.base;

import com.demo.base.doc.EnableAPIScanner;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
@EnableAPIScanner
public class BaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
        log.info("App is Running on {} {}, PWD = {}",System.getProperty("os.name"),System.getProperty("os.arch"),System.getProperty("user.dir"));
    }
}