package com.bee.bin;
import com.bee.server.JavaBee;
import com.bee.controller.OrderController;
public class StartServer {
    public static void main(String[] args){
        new JavaBee()
            .Router(new OrderController())
            .Router("/admin/list", context -> {
                context.print("进入了管理列表");
            })
            .Router("/user/list",context->{
               context.print("进入了用户列表");
            })
            .startServer();
    }
}
