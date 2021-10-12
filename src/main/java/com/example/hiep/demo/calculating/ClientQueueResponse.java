package com.example.hiep.demo.calculating;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQMessage;
import io.vertx.rabbitmq.RabbitMQOptions;

public class ClientQueueResponse extends AbstractVerticle {

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
        client.start(startResult -> {
            if (startResult.succeeded()) {
                System.out.println("RabiitMQ successfully connected");

                client.basicConsumer(QUEUE_REQUEST, queueOptions, basic -> {
                    if (basic.succeeded()) {
                        RabbitMQConsumer consumer = basic.result();
                        System.out.println("");
                        System.out.println("Tag - " + QUEUE_REQUEST + ": " + consumer.consumerTag());
                        System.out.println("name - " + QUEUE_REQUEST + ": " + consumer.queueName());

                        consumer.handler(consumerHandler -> {
//                            System.out.println("");
//                            System.out.println("Payload - " + QUEUE_REQUEST + ": " + consumerHandler.body());
//                            System.out.println("Properties - " + QUEUE_REQUEST + ": " + consumerHandler.properties().toString());
                            JsonObject payload = consumerHandler.body().toJsonObject();
                            String resultCalculate = calculate(payload.getString("first"), payload.getString("second"), payload.getString("operater"));
                            responseResult(client, resultCalculate);
                        });
                    } else {
                        System.out.println("Consumer failed - " + QUEUE_REQUEST + ": " + basic.cause().getMessage());
                    }
                });

//                client.basicGet(QUEUE_REQUEST, false, getResult -> {
//                    if (getResult.succeeded()) {
//                        RabbitMQMessage message = getResult.result();
//                        System.out.println("");
//                        System.out.println("Get Received Tag: " + message.consumerTag());
//                        System.out.println("Get Received Properties: " + message.properties().toString());
//                        System.out.println("Get Received Json: " + message.body().toJsonObject());
//                    } else {
//                        System.out.println("Get result failed: " + getResult.cause().getMessage());
//                    }
//                });
            } else {
                System.out.println("Fail to connect to RabbitMQ: " + startResult.cause().getMessage());
            }
        });
    }

    private void responseResult(RabbitMQClient client, String result) {
        client.start(startResult -> {
            if (startResult.succeeded()) {
                JsonObject response = new JsonObject();
                response.put("result", result);
                client.basicPublish("", QUEUE_RESPONSE, response.toBuffer(), publish -> {
                    if (publish.succeeded()) {
                        System.out.println("Response basic success! - " + result);
                    } else {
                        System.out.println("Response basic failed - " + QUEUE_RESPONSE + ": " + publish.cause().getMessage());
                    }
                });
            } else {
                System.out.println("Fail to connect to RabbitMQ: " + startResult.cause().getMessage());
            }
        });
    }

    private final String ADD = "add";
    private final String SUBTRACT = "subtract";
    private final String MULTIPLY = "multiply";
    private final String DIVIDE = "divide";

    private String calculate(String first, String second, String operater) {
        double a;
        double b;
        String result;
        try {
            a = Double.parseDouble(first);
        } catch (NumberFormatException e) {
            a = 0;
        }
        try {
            b = Double.parseDouble(second);
        } catch (NumberFormatException e) {
            b = 0;
        }
        if (!operater.matches("^(add|subtract|multiply|divide)$")) {
            result = "The operator is not correct.\n"
                    + "Only add, subtract, multiply, divide (+, -, *, /)";
        } else {
            switch (operater) {
                case ADD:
                    result = String.valueOf(a + b);
                    break;
                case SUBTRACT:
                    result = String.valueOf(a - b);
                    break;
                case MULTIPLY:
                    result = String.valueOf(a * b);
                    break;
                case DIVIDE:
                    if (b == 0) {
                        result = "The devided number (second) must not be 0";
                    } else {
                        result = String.valueOf(a / b);
                    }
                    break;
                default:
                    result = "The operator is not correct.\n"
                            + "Only add, subtract, multiply, divide (+, -, *, /)";
            }
        }

        return String.valueOf(result);
    }
}
