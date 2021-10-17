package com.example.hiep.demo.rabbitmq;

import com.example.hiep.demo.MainVerticle;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQOptions;
import java.util.UUID;

public class ClientQueueRequest extends AbstractVerticle {

    private final String QUEUE_REQUEST = "calculate.queue.request";
    private final String QUEUE_RESPONSE = "queue.request.result";

    private JWTAuth provider;

    @Override
    public void start() throws Exception {
        provider = MainVerticle.initJWT(vertx);

        RabbitMQOptions options = new RabbitMQOptions();
        // full amqp uri
//        options.setUri("amqp://admin:admin@localhost:5672/test");

        // custom parameter
        options.setUser("admin");
        options.setPassword("admin");
        options.setHost("localhost");
        options.setPort(5672);
        options.setVirtualHost("test");
//        options.setAutomaticRecoveryEnabled(true);
//        options.setReconnectAttempts(2);              // max reconnect attempt are 2
        options.setConnectionTimeout(1_000);          // milisecond
//          options.setAddresses(Arrays.asList(Address.parseAddresses("firstHost,secondHost:5672")));  // Set multiples addresses to connect to a cluster

        RabbitMQClient client = RabbitMQClient.create(vertx, options);

        // connect
        client.start(result -> {
            if (result.succeeded()) {
                System.out.println("RabiitMQ successfully connected");

                sendRequest(client);
                receiveResult(client);
            } else {
                System.out.println("Fail to connect to RabbitMQ: " + result.cause().getMessage());
            }
        });
    }

    private void sendRequest(RabbitMQClient client) {
        BasicProperties properties = new AMQP.BasicProperties().builder()
                .replyTo(QUEUE_RESPONSE)
                .correlationId(UUID.randomUUID().toString())
                .build();

        String token = provider.generateToken(
                new JsonObject().put("first", "100")
                        .put("second", "200")
                        .put("operater", "subtract"),
                new JWTOptions().setAlgorithm("RS256"));
        Buffer buffer = Buffer.buffer(token);

        client.basicPublish("", QUEUE_REQUEST, properties, buffer, pubHandler -> {
            if (pubHandler.succeeded()) {
                System.out.println("Send request success");
            } else {
                System.out.println("Send request failed");
            }
        });
    }

    private void receiveResult(RabbitMQClient client) {
        client.queueDeclare(QUEUE_RESPONSE, false, false, true);

        client.basicConsumer(QUEUE_RESPONSE, basicConsumerHandler -> {
            if (basicConsumerHandler.succeeded()) {
                RabbitMQConsumer consumer = basicConsumerHandler.result();
                consumer.handler(consumerHandler -> {
                    System.out.println("Result: " + consumerHandler.body().toString());
                    client.stop();
                });
            }
        });
    }
}
