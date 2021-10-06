package com.example.hiep.demo.error;

import com.example.hiep.demo.IRequestHandler;
import com.example.hiep.demo.MainVerticle;
import static com.example.hiep.demo.MainVerticle.ROUTER;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class HandleErrorVerticle extends MainVerticle implements IRequestHandler {

    private void doError(RoutingContext context) {
        Error error = new Error();
        error.setErrStatus(500);
        error.setErrMsg(context.failure().toString());
        JsonObject json = JsonObject.mapFrom(error);
        context.response().end(json.encodePrettily());
    }

    @Override
    public void handleRequest() {
        ROUTER.errorHandler(500, f -> doError(f));
    }
}