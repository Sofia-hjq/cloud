package com.moti.file.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName: WebConfig
 * @Description: Web配置类
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Configuration
public class WebConfig {

    /**
     * 配置RestTemplate，支持负载均衡
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 