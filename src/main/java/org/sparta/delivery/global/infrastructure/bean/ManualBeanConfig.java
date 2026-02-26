package org.sparta.delivery.global.infrastructure.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManualBeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om;
    }
}
