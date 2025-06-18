package com.moti.file.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @ClassName: WebConfig
 * @Description: Web配置类
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置RestTemplate，支持负载均衡
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // CSS资源映射
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
                
        // JS资源映射  
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
                
        // 图片资源映射
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
        
        // u-admin静态资源映射
        registry.addResourceHandler("/u-admin/**")
                .addResourceLocations("classpath:/static/u-admin/");
                
        // 插件资源映射
        registry.addResourceHandler("/plug-ins/**")
                .addResourceLocations("classpath:/static/plug-ins/");
                
        // 允许访问模板资源
        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/templates/");
    }

    /**
     * 配置CORS跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置视图控制器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }
} 