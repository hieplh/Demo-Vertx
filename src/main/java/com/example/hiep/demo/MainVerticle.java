package com.example.hiep.demo;

import com.example.hiep.demo.calculating.ClientExchangeRequest;
import com.example.hiep.demo.calculating.ClientQueueRequest;
import com.example.hiep.demo.calculating.ClientQueueResponse;
import io.vertx.core.Vertx;

public class MainVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ClientQueueRequest());
        vertx.deployVerticle(new ClientExchangeRequest());
        vertx.deployVerticle(new ClientQueueResponse());
    }
}
