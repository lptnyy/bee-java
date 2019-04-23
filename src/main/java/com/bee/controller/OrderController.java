package com.bee.controller;

import com.bee.server.Server;
import com.bee.server.router.Controller;

public class OrderController implements Controller {

    @Override
    public void init(Server server) {
        server.Router("/order", controller -> {
            controller.print("234");
        });
    }
}
