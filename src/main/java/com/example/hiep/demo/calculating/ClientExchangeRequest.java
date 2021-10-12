package com.example.hiep.demo.calculating;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

public class ClientExchangeRequest extends AbstractVerticle {

    private final String EXCHANGE_REQUEST = "calculate.exchange.request";
    private final String EXCHANGE_RESPONSE = "calculate.exchange.response";

    @Override
    public void start() throws Exception {
        RabbitMQOptions options = new RabbitMQOptions();
        // full amqp uri
        options.setUri("amqp://admin:admin@localhost:5672/test");

        // custom parameter
//        options.setUser("username");
//        options.setPassword("password");
//        options.setHost("localhost");
//        options.setPort(5672);
//        options.setVirtualHost("vhost");
//        options.setAutomaticRecoveryEnabled(true);
//        options.setReconnectAttempts(2);              // max reconnect attempt are 2
//        options.setConnectionTimeout(1_000);          // milisecond
//          options.setAddresses(Arrays.asList(Address.parseAddresses("firstHost,secondHost:5672")));  // Set multiples addresses to connect to a cluster
        QueueOptions queueOptions = new QueueOptions();
        queueOptions.setMaxInternalQueueSize(1000);

        RabbitMQClient client = RabbitMQClient.create(vertx, options);

        client.start(result -> {
            if (result.succeeded()) {
                System.out.println("RabiitMQ successfully connected");

                JsonObject jsonConfirmSelectExchange = new JsonObject();
                jsonConfirmSelectExchange.put("first", "8");
                jsonConfirmSelectExchange.put("second", "8");
                jsonConfirmSelectExchange.put("operater", "multiply");
                Buffer bufferConfirmSelectExchange = Buffer.buffer(jsonConfirmSelectExchange.encodePrettily());
                client.confirmSelect(confirmResult -> {
                    if (confirmResult.succeeded()) {
                        client.basicPublish(EXCHANGE_REQUEST, "", bufferConfirmSelectExchange, pubResult -> {
                            if (pubResult.succeeded()) {
                                client.waitForConfirms(waitResult -> {
                                    if (waitResult.succeeded()) {
                                        System.out.println("Message confirm exchange Published");
                                    } else {
                                        System.out.println("Message confirm exchange is not published: " + waitResult.cause().getMessage());
                                    }
                                });
                            } else {
                                System.out.println("Fail to publish confirm exchange message: " + pubResult.cause().getMessage());
                            }
                        });
                    } else {
                        System.out.println("Confirm Select exchange failed: " + confirmResult.cause().getMessage());
                    }
                });

                JsonObject jsonBasicPublishExchange = new JsonObject();
                jsonBasicPublishExchange.put("first", "10");
                jsonBasicPublishExchange.put("second", "21");
                jsonBasicPublishExchange.put("operater", "divide");
                Buffer bufferBasicPublishExchange = Buffer.buffer(jsonBasicPublishExchange.encodePrettily());
                client.basicPublish(EXCHANGE_REQUEST, "", bufferBasicPublishExchange, pubResult -> {
                    if (pubResult.succeeded()) {
                        client.waitForConfirms(waitResult -> {
                            if (waitResult.succeeded()) {
                                System.out.println("Message basic exchange Published");
                            } else {
                                System.out.println("Message basic exchange is not published: " + waitResult.cause().getMessage());
                            }
                        });
                    } else {
                        System.out.println("Fail to publish basic exchange message: " + pubResult.cause().getMessage());
                    }
                });

//                client.basicConsumer(EXCHANGE_REQUEST, queueOptions, basic -> {
//                    if (basic.succeeded()) {
//                        RabbitMQConsumer consumer = basic.result();
//                        System.out.println("");
//                        System.out.println("Tag - " + EXCHANGE_REQUEST + ": " + consumer.consumerTag());
//                        System.out.println("name - " + EXCHANGE_REQUEST + ": " + consumer.queueName());
//
//                        consumer.handler(consumerHandler -> {
//                            System.out.println("");
//                            System.out.println("Payload - " + EXCHANGE_REQUEST + ": " + consumerHandler.body());
//                            System.out.println("Properties - " + EXCHANGE_REQUEST + ": " + consumerHandler.properties().toString());
//                        });
//                    } else {
//                        System.out.println("Consumer failed " + EXCHANGE_REQUEST + ": " + basic.cause().getMessage());
//                    }
//                });
            } else {
                System.out.println("Fail to connect to RabbitMQ: " + result.cause().getMessage());
            }
        });
    }
}
