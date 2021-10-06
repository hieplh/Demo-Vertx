package com.example.hiep.demo;

import com.example.hiep.demo.calculating.Calculating;
import com.example.hiep.demo.error.HandleErrorVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import java.util.List;

public class MainVerticle extends AbstractVerticle {

    public static Router ROUTER;
    public static HttpServer SERVER;
    public static FileSystem FILE_SYSTEM;

    public static String HOST = "http://localhost";
    public final String RESOURCER_DEFAULT = "./src/main/resources/";

    public final List<IRequestHandler> listRequestHandler = new ArrayList<>();
    
    @Override
    public void start() throws Exception {
        SERVER = vertx.createHttpServer();
        ROUTER = Router.router(vertx);
        ROUTER.route().handler(BodyHandler.create());
        FILE_SYSTEM = vertx.fileSystem();

        initHandlerRequest();
        
        SERVER.requestHandler(ROUTER)
                .listen(8080, http -> {
                    if (http.succeeded()) {
                        HOST = HOST + ":" + SERVER.actualPort();
                        context.put("host", HOST);
                        System.out.println("HTTP Server started on port " + SERVER.actualPort());
                    } else {
                        System.out.println(http.cause().getMessage());
                    }
                });
    }
    
    private void initHandlerRequest() {
        listRequestHandler.add(new HandleErrorVerticle());
        listRequestHandler.add(new Calculating());

        listRequestHandler.forEach(e -> e.handleRequest());
    }
}
