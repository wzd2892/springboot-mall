package com.wzd.newbeemall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wzd.newbeemall.mapper")

public class NewbeeMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewbeeMallApplication.class, args);
    }

}
