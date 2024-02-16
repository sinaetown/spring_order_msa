package com.encore.ordering.common;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    
    @Bean
//    eureka에 등록된 서비스명을 사용해서 내부 서비스 호출할 수 있게 해주는 annotation
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
