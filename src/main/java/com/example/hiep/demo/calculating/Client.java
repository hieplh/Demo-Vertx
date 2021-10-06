package com.example.hiep.demo.calculating;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class Client extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        HttpClientOptions options = new HttpClientOptions();
        options.setTrustAll(true)
                .setDefaultHost("localhost")
                .setDefaultPort(8080);

        HttpClient client = vertx.createHttpClient(options);
        client.request(HttpMethod.GET, "/calculate/add/1/2")
                .compose(request ->
                        request.send()
                                .compose(response -> response.body()))
                .onSuccess(result -> System.out.println(result.toString()))
                .onFailure(e -> System.out.println("Error: " + e.getMessage()));

        client.request(HttpMethod.POST, "/calculate")
                .compose(request -> {
                    JsonObject json = new JsonObject();
                    json.put("first", 2);
                    json.put("second", 3);
                    json.put("operater", "multiply");
                    return request.send(json.toBuffer());
                }).compose(response -> response.body())
                .onSuccess(result -> System.out.println(result))
                .onFailure(e -> System.out.println("Error: " + e.getMessage()));
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Client());
    }
}
