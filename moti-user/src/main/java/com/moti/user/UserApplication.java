package com.moti.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName: UserApplication
 * @Description: 用户服务启动类
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan("com.moti.user.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
} 