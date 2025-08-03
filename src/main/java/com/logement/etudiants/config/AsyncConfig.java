package com.logement.etudiants.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; /**
 * Configuration pour les tâches asynchrones
 */
/*
@Configuration
@org.springframework.scheduling.annotation.EnableAsync
@org.springframework.scheduling.annotation.EnableScheduling
public class AsyncConfig implements org.springframework.scheduling.annotation.AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public java.util.concurrent.Executor taskExecutor() {
        org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor executor =
                new org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        return executor;
    }

    @Override
    public java.util.concurrent.Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            System.err.println("Erreur dans la tâche asynchrone: " + throwable.getMessage());
            throwable.printStackTrace();
        };
    }
}

 */
