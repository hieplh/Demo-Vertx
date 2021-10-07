package com.example.hiep.demo.calculating;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class Client extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();

        JsonObject example1 = new JsonObject();
        example1.put("first", 10);
        example1.put("second", 20);
        example1.put("operater", "add");
        
        eventBus.request("calculating.basic", example1, response -> {
            if (response.succeeded()) {
                System.out.println("Result_1: " + response.result().body());
            } else {
                System.out.println("Lost message_1, cause: " + response.cause().toString());
            }
        });
        
        JsonObject example2 = new JsonObject();
        example2.put("first", 10);
        example2.put("second", 20);
        example2.put("operater", "divide");
        eventBus.request("calculating.basic", example2, response -> {
            if (response.succeeded()) {
                System.out.println("Result_2: " + response.result().body());
            } else {
                System.out.println("Lost message_2, cause: " + response.cause().toString());
            }
        });
    }
}
