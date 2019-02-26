package com.apexsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication
@ServletComponentScan
public class AmsApplication{
    public static void main(String[] args) {
        SpringApplication.run(AmsApplication.class, args);
    }
}
