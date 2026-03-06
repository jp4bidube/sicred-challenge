package com.challenge.voteconsumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        // Tenta reprocessar 3 vezes com intervalo de 1 segundo
        // Se falhar, envia para o tópico DLT (Dead Letter Topic)
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template),
                new FixedBackOff(1000L, 3));
    }
}
