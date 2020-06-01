package org.example.rabbitmq;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.RabbitMQContainer;

import static org.hamcrest.CoreMatchers.equalTo;

@Import(RabbitMQAdminConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleApplication.class)
@ActiveProfiles({"default"})
public class SampleApplicationDefaultProfileTest {

    @Autowired
    RabbitMQContainer rabbitMQContainer;

    @Value("${embedded.rabbitmq.host}")
    String host;

    @Value("${embedded.rabbitmq.user}")
    String user;

    @Value("${embedded.rabbitmq.password}")
    String password;

    Integer apiPort;

    @Before
    public void setup() {
        apiPort = rabbitMQContainer.getMappedPort(15672);
    }

    @Test
    public void should_create_expected_queue() throws Exception {
        RestAssured.given()
                .auth().basic(user, password)
                .baseUri("http://" + host + ":" + apiPort)
                .when()
                .log().all()
                .get("/api/queues")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].arguments.x-dead-letter-exchange", equalTo("DLX"))
                .body("[0].arguments.x-dead-letter-routing-key", equalTo("live.fpl.sample-channel"))
                .body("[0].auto_delete", equalTo(Boolean.FALSE))
                .body("[0].durable", equalTo(Boolean.TRUE))
                .body("[0].exclusive", equalTo(Boolean.FALSE))
                .body("[0].name", equalTo("live.fpl.sample-channel"))
                .body("[0].vhost", equalTo("/"))

                .body("[1].auto_delete", equalTo(Boolean.FALSE))
                .body("[1].durable", equalTo(Boolean.TRUE))
                .body("[1].exclusive", equalTo(Boolean.FALSE))
                .body("[1].name", equalTo("live.fpl.sample-channel.dlq"))
                .body("[1].vhost", equalTo("/"));
    }

}
