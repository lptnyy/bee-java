package com.bee.controlle;

import com.bee.server.Server;
import com.bee.server.router.Controlle;

public class OrderControlle implements Controlle {

    @Override
    public void init(Server server) {
        server.Router("/order", controller -> {
            controller.print("234");
        });
    }
}
