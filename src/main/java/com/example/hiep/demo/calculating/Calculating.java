package com.example.hiep.demo.calculating;

import com.example.hiep.demo.IRequestHandler;
import com.example.hiep.demo.MainVerticle;
import static com.example.hiep.demo.MainVerticle.ROUTER;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class Calculating extends MainVerticle implements IRequestHandler {

    private final String ADD = "add";
    private final String SUBTRACT = "subtract";
    private final String MULTIPLY = "multiply";
    private final String DEVIDE = "divide";

    private void doGet(RoutingContext context) {
        String first = context.request().getParam("first");
        String second = context.request().getParam("second");
        String operater = context.request().getParam("operater");

        String result = calculate(first, second, operater);
        context.response().end(String.valueOf(result));
    }

    private void doPost(RoutingContext context) {
        Buffer buffer = context.getBody();
        JsonObject json = new JsonObject(buffer);
        String first = json.getString("first");
        String second = json.getString("second");
        String operater = json.getString("operater");

        String result = calculate(first, second, operater);
        context.response().end(String.valueOf(result));
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
//                    if (b == 0) {
//                        result = "The devided number (second) must not be 0";
//                    } else {
//                        result = String.valueOf(a / b);
//                    }
                    result = String.valueOf(a / b);
                    break;
                default:
                    result = "The operator is not correct.\n"
                            + "Only add, subtract, multiply, devide (+, -, *, /)";
            }
        }

        return String.valueOf(result);
    }

    @Override
    public void handleRequest() {
        ROUTER.route(HttpMethod.GET, "/calculate/:operater/:first/:second")
                .handler(context -> doGet(context));
        ROUTER.route(HttpMethod.POST, "/calculate")
                .handler(context -> doPost(context));
    }
}
