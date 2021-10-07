package com.example.hiep.demo.calculating;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class Server extends AbstractVerticle {

    private final String ADD = "add";
    private final String SUBTRACT = "subtract";
    private final String MULTIPLY = "multiply";
    private final String DEVIDE = "divide";

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();

        eventBus.consumer("calculating.basic", message -> {
            System.out.println("I have received a message: " + message.body());
            JsonObject json = JsonObject.mapFrom(message.body());
            String first = json.getString("first");
            String second = json.getString("second");
            String operater = json.getString("operater");

            String result = calculate(first, second, operater);
            message.reply(result);
        });
    }

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
                    + "Only add, subtract, multiply, devide (+, -, *, /)";
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
                case DEVIDE:
                    if (b == 0) {
                        result = "The devided number (second) must not be 0";
                    } else {
                        result = String.valueOf(a / b);
                    }
                    break;
                default:
                    result = "The operator is not correct.\n"
                            + "Only add, subtract, multiply, devide (+, -, *, /)";
            }
        }

        return String.valueOf(result);
    }
}
