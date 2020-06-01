# What is it

keywords: **SpringBoot 1.5.9**, **RabbitMQ**, **testcontainers**, **integrations tests**, **docker**.

This sample describes how to run SpringBoot tests with a real rabbitmq to ensure that expected queues will be created following application setup. 

`testcontainers` is used to manage docker virtual machine creation. It expose random port to host. It allows injection of dynamic variables. You can have a look to test part to see it.

## How to run

From the console under the project run this:

```bash
docker pull rabbitmq:3.7-management-alpine && \
  docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; \
  export TESTCONTAINERS_RYUK_DISABLED=true; \
  mvn clean test
```

## Test additional information

I use a rabbimq image with management plugin - which expose a rest api - to query queues creation and binding in tests after launching the application depending on profiles used.

### TESTCONTAINERS_RYUK_DISABLED
> Ryuk is a specific container launched by `testcontainers` to manage container lifecycle (CREATE, RUN and DESTROY) between each test.

As we defined `TESTCONTAINERS_RYUK_DISABLED` as disabled, docker containers will remain running after running tests.
1. I found it convenient for debugging purpose when creating my tests to understand how rabbitmq works;
1. I have got a bug on my computer preventing container to be managed by `RYUK`. Maybe this is du to a permission issue as it need to be run as privileged container (https://www.testcontainers.org/supported_docker_environment/continuous_integration/bitbucket_pipelines/)
I should check this link https://github.com/testcontainers/testcontainers-java/issues/1274 however if running in an OKD env I should not be able to run it as privileged container.

## References

> RabbitMQ rest api : https://pulse.mozilla.org/api/
>
> testcontainers : https://www.testcontainers.org/
>
> testcontainers Spring Boot : https://github.com/testcontainers/testcontainers-spring-boot

