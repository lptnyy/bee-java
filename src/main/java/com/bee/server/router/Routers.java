package com.bee.server.router;
import java.util.HashMap;
import java.util.Map;

public class Routers {
    static Map<String, Context> routers = new HashMap<>();
    static Routers routersInstance = null;

    /**
     * 单例模式
     * @return
     */
    public static Routers getInstance(){
        if (routersInstance == null) {
            synchronized (Routers.class) {
                if (routersInstance == null) {
                    routersInstance = new Routers();
                }
            }
        }
        return routersInstance;
    }

    public synchronized Routers Router(String path, Context context){
        routers.put(path,context);
        return this;
    }

    public Context getRouter(String path){
        return routers.get(path);
    }
}
