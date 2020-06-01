package org.example.rabbitmq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface InputBinder {

    @Input("sampleChannel")
    MessageChannel sampleChannel();

}
