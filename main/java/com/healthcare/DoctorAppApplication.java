package com.healthcare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.healthcare.feature")
public class DoctorAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(DoctorAppApplication.class, args);
    }
}
