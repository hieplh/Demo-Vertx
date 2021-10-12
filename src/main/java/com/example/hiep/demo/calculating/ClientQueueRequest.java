package com.example.hiep.demo.calculating;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQMessage;
import io.vertx.rabbitmq.RabbitMQOptions;

public class ClientQueueRequest extends AbstractVerticle {

    private final String QUEUE_REQUEST = "calculate.queue.request";
    private final String QUEUE_RESPONSE = "calculate.queue.response";

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

        // connect
        client.start(result -> {
            if (result.succeeded()) {
                System.out.println("RabiitMQ successfully connected");

                JsonObject jsonConfirmSelect = new JsonObject();
                jsonConfirmSelect.put("first", "2");
                jsonConfirmSelect.put("second", "3");
                jsonConfirmSelect.put("operater", "add");
                Buffer bufferConfirmSelect = Buffer.buffer(jsonConfirmSelect.encodePrettily());
                client.confirmSelect(confirmResult -> {
                    if (confirmResult.succeeded()) {
                        client.basicPublish("", QUEUE_REQUEST, bufferConfirmSelect, pubResult -> {
                            if (pubResult.succeeded()) {
                                client.waitForConfirms(waitResult -> {
                                    if (waitResult.succeeded()) {
                                        System.out.println("Message confirm Published");
                                    } else {
                                        System.out.println("Message confirm is not published: " + waitResult.cause().getMessage());
                                    }
                                });
                            } else {
                                System.out.println("Fail to publish confirm message: " + pubResult.cause().getMessage());
                            }
                        });
                    } else {
                        System.out.println("Confirm Select failed: " + confirmResult.cause().getMessage());
                    }
                });

                JsonObject jsonBasicPublish = new JsonObject();
                jsonBasicPublish.put("first", "5");
                jsonBasicPublish.put("second", "4");
                jsonBasicPublish.put("operater", "subtract");
                Buffer bufferBasicPublish = Buffer.buffer(jsonBasicPublish.encodePrettily());
                client.basicPublish("", QUEUE_REQUEST, bufferBasicPublish, pubResult -> {
                    if (pubResult.succeeded()) {
                        client.waitForConfirms(waitResult -> {
                            if (waitResult.succeeded()) {
                                System.out.println("Message basic Published");
                            } else {
                                System.out.println("Message basic is not published: " + waitResult.cause().getMessage());
                            }
                        });
                    } else {
                        System.out.println("Fail to publish basic message: " + pubResult.cause().getMessage());
                    }
                });

                client.basicConsumer(QUEUE_RESPONSE, queueOptions, basic -> {
                    if (basic.succeeded()) {
                        RabbitMQConsumer consumer = basic.result();
                        System.out.println("");
                        System.out.println("Tag - " + QUEUE_RESPONSE + ": " + consumer.consumerTag());
                        System.out.println("name - " + QUEUE_RESPONSE + ": " + consumer.queueName());

                        consumer.handler(consumerHandler -> {
                            System.out.println("");
//                            System.out.println("Payload - " + QUEUE_RESPONSE + ": " + consumerHandler.body());
//                            System.out.println("Properties - " + QUEUE_RESPONSE + ": " + consumerHandler.properties().toString());
                            JsonObject response = consumerHandler.body().toJsonObject();
                            System.out.println("Result: " + response.getString("result"));
                        });
                    } else {
                        System.out.println("Consumer failed - " + QUEUE_RESPONSE + ": " + basic.cause().getMessage());
                    }
                });
            } else {
                System.out.println("Fail to connect to RabbitMQ: " + result.cause().getMessage());
            }

            //            client.basicGet(KEY_PUBLISH, true, getResult -> {
//                if (getResult.succeeded()) {
//                    RabbitMQMessage message = getResult.result();
//                    System.out.println("Server received Tag: " + message.consumerTag());
//                    System.out.println(": " + message.properties().toString());
//                } else {
//                    System.out.println("Get result failed: " + getResult.cause().getMessage());
//                }
//            });
        });
    }
}
