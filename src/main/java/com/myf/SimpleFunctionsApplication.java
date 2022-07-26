package com.myf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com/myf/mapper")
public class SimpleFunctionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleFunctionsApplication.class, args);
    }

}
