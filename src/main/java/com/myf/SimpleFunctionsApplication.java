package com.myf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com/myf/mapper")
@EnableAspectJAutoProxy
public class SimpleFunctionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleFunctionsApplication.class, args);
    }

}
