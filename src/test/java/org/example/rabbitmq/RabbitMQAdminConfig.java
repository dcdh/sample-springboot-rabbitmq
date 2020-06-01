package org.example.rabbitmq;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.RabbitMQContainer;

@TestConfiguration
public class RabbitMQAdminConfig {

    @Autowired
    RabbitMQContainer rabbitMQContainer;

    @Value("${embedded.rabbitmq.port}")
    Integer port;

    @Value("${embedded.rabbitmq.host}")
    String host;

    @Value("${embedded.rabbitmq.vhost}")
    String vhost;

    @Value("${embedded.rabbitmq.user}")
    String user;

    @Value("${embedded.rabbitmq.password}")
    String password;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(user);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        return connectionFactory;
    }

}
