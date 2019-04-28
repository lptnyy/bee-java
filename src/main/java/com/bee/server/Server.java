package com.bee.server;

import com.bee.server.router.Context;
import com.bee.server.router.Controller;
public interface Server {
    public void startServer();
    public Server Router(String path, Context context);
    public Server Router(Controller controlle);
}
