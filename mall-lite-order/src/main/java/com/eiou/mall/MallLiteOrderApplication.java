package com.eiou.mall;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.eiou.mall", annotationClass = Mapper.class)
@SpringBootApplication
public class MallLiteOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallLiteOrderApplication.class, args);
    }
}
