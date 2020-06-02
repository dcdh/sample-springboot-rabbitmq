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
@ActiveProfiles({"default", "with-replay"})
public class SampleApplicationWithReplayTest {

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
                .body("[1].vhost", equalTo("/"))

                .body("[2].arguments.x-dead-letter-exchange", equalTo("DLX"))
                .body("[2].arguments.x-dead-letter-routing-key", equalTo("replay.fpl.sample-channel"))
                .body("[2].auto_delete", equalTo(Boolean.FALSE))
                .body("[2].durable", equalTo(Boolean.TRUE))
                .body("[2].exclusive", equalTo(Boolean.FALSE))
                .body("[2].name", equalTo("replay.fpl.sample-channel"))
                .body("[2].vhost", equalTo("/"))

                .body("[3].auto_delete", equalTo(Boolean.FALSE))
                .body("[3].durable", equalTo(Boolean.TRUE))
                .body("[3].exclusive", equalTo(Boolean.FALSE))
                .body("[3].name", equalTo("replay.fpl.sample-channel.dlq"))
                .body("[3].vhost", equalTo("/"));
    }

    @Test
    public void should_bind() {
        RestAssured.given()
                .auth().basic(user, password)
                .baseUri("http://" + host + ":" + apiPort)
                .when()
                .log().all()
                .get("/api/bindings")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].source", equalTo(""))
                .body("[0].vhost", equalTo("/"))
                .body("[0].destination", equalTo("live.fpl.sample-channel"))
                .body("[0].destination_type", equalTo("queue"))
                .body("[0].routing_key", equalTo("live.fpl.sample-channel"))
                .body("[0].properties_key", equalTo("live.fpl.sample-channel"))

                .body("[1].source", equalTo(""))
                .body("[1].vhost", equalTo("/"))
                .body("[1].destination", equalTo("live.fpl.sample-channel.dlq"))
                .body("[1].destination_type", equalTo("queue"))
                .body("[1].routing_key", equalTo("live.fpl.sample-channel.dlq"))
                .body("[1].properties_key", equalTo("live.fpl.sample-channel.dlq"))

                .body("[2].source", equalTo(""))
                .body("[2].vhost", equalTo("/"))
                .body("[2].destination", equalTo("replay.fpl.sample-channel"))
                .body("[2].destination_type", equalTo("queue"))
                .body("[2].routing_key", equalTo("replay.fpl.sample-channel"))
                .body("[2].properties_key", equalTo("replay.fpl.sample-channel"))

                .body("[3].source", equalTo(""))
                .body("[3].vhost", equalTo("/"))
                .body("[3].destination", equalTo("replay.fpl.sample-channel.dlq"))
                .body("[3].destination_type", equalTo("queue"))
                .body("[3].routing_key", equalTo("replay.fpl.sample-channel.dlq"))
                .body("[3].properties_key", equalTo("replay.fpl.sample-channel.dlq"))

                .body("[4].source", equalTo("DLX"))
                .body("[4].vhost", equalTo("/"))
                .body("[4].destination", equalTo("live.fpl.sample-channel.dlq"))
                .body("[4].destination_type", equalTo("queue"))
                .body("[4].routing_key", equalTo("live.fpl.sample-channel"))
                .body("[4].properties_key", equalTo("live.fpl.sample-channel"))

                .body("[5].source", equalTo("DLX"))
                .body("[5].vhost", equalTo("/"))
                .body("[5].destination", equalTo("replay.fpl.sample-channel.dlq"))
                .body("[5].destination_type", equalTo("queue"))
                .body("[5].routing_key", equalTo("replay.fpl.sample-channel"))
                .body("[5].properties_key", equalTo("replay.fpl.sample-channel"))

                .body("[6].source", equalTo("live.fpl"))
                .body("[6].vhost", equalTo("/"))
                .body("[6].destination", equalTo("live.fpl.sample-channel"))
                .body("[6].destination_type", equalTo("queue"))
                .body("[6].routing_key", equalTo("live"))
                .body("[6].properties_key", equalTo("live"))

                .body("[7].source", equalTo("replay.fpl"))
                .body("[7].vhost", equalTo("/"))
                .body("[7].destination", equalTo("replay.fpl.sample-channel"))
                .body("[7].destination_type", equalTo("queue"))
                .body("[7].routing_key", equalTo("live"))
                .body("[7].properties_key", equalTo("live"))
        ;
    }

}
