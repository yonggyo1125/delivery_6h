package org.sparta.delivery.global.infrastructure.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class EventConfig implements AsyncConfigurer {

    @Autowired
    private ApplicationContext ctx;

    @Bean
    public InitializingBean eventsInitializer() {
        return () -> Events.setPublisher(ctx);
    }

    // 비동기 적용시 생성될 스레드 풀 설정
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);        // 기본 스레드 수
        executor.setMaxPoolSize(50);        // 최대 스레드 수
        executor.setQueueCapacity(100);     // 대기 큐 용량
        executor.setThreadNamePrefix("Async-"); // 스레드 이름 접두사
        executor.initialize();
        return executor;
    }
}
