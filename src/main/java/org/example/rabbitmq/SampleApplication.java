package org.example.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding({InputBinder.class})
public class SampleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
