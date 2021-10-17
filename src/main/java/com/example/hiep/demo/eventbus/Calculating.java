package com.example.hiep.demo.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class Calculating extends AbstractVerticle {

    private final String EVENTBUS_CONSUME = "calculate.eventbus.request";

    private final String ADD = "add";
    private final String SUBTRACT = "subtract";
    private final String MULTIPLY = "multiply";
    private final String DIVIDE = "divide";

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(EVENTBUS_CONSUME, consumerHandler -> {
            JsonObject payload = (JsonObject) consumerHandler.body();
            String result = calculate(payload.getString("first"),
                    payload.getString("second"),
                    payload.getString("operater"));
            
            consumerHandler.reply(result);
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
