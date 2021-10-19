package com.example.hiep.demo.rabbitmq;

import com.rabbitmq.client.BasicProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQOptions;
import java.util.Base64;

public class ClientQueueResponse extends AbstractVerticle {

    private final String QUEUE_REQUEST = "calculate.queue.request";

    private final String EVENTBUS_REQUEST = "calculate.eventbus.request";

    private final int HEADER = 1;
    private final int PAYLOAD = 2;
    private final int SIGNATURE = 3;

    @Override
    public void start() throws Exception {
        RabbitMQOptions options = new RabbitMQOptions();
        // full amqp uri
        options.setUri("amqp://admin:admin@localhost:5672/test");

        RabbitMQClient client = RabbitMQClient.create(vertx, options);
        client.start(startResult -> {
            if (startResult.succeeded()) {
                System.out.println("RabiitMQ successfully connected");

                consumeRequest(client);
            }
        });
    }

    private void consumeRequest(RabbitMQClient client) {
        client.basicConsumer(QUEUE_REQUEST, basicConsumerHandler -> {
            if (basicConsumerHandler.succeeded()) {
                RabbitMQConsumer consumer = basicConsumerHandler.result();
                consumer.handler(consumerHandler -> {
                    Buffer content = consumerHandler.body();
                    BasicProperties pSrc = consumerHandler.properties();
                    if (pSrc == null || pSrc.getCorrelationId() == null) {
                        System.out.println("Result: " + content);
                    } else {
                        String payloadString = (String) decode(content.toString(), PAYLOAD);
                        JsonObject payload = new JsonObject(payloadString);

                        dispatch(client, pSrc, payload);
                    }
                });
            }
        });
    }

    private Object decode(String token, int type) {
        Base64.Decoder decoder = Base64.getDecoder();
        return type != PAYLOAD ? null : new String(decoder.decode(token.split("\\.")[1]));
    }

    private void dispatch(RabbitMQClient client, BasicProperties pSrc, JsonObject payload) {
        EventBus eventBus = vertx.eventBus();
        eventBus.request(EVENTBUS_REQUEST, payload, reply -> {
            if (reply.succeeded()) {
                String result = (String) reply.result().body();
                client.basicPublish("", pSrc.getReplyTo(), Buffer.buffer(result));
            } else {
                client.basicPublish("", pSrc.getReplyTo(), Buffer.buffer("Server got something wrong"));
            }
        });
    }
}
