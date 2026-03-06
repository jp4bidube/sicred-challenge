package com.challenge.voteconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VoteConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoteConsumerApplication.class, args);
    }

}
