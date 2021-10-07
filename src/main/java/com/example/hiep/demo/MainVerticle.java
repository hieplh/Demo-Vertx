package com.example.hiep.demo;

import com.example.hiep.demo.calculating.Client;
import com.example.hiep.demo.calculating.Server;
import io.vertx.core.Vertx;

public class MainVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Server());
        vertx.deployVerticle(new Client());
    }
}
