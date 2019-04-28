package com.bee.server.http;

import com.bee.properties.ServerProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpSessionManger {
    private static Logger logger = LogManager.getLogger(HttpSessionManger.class);
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    static Map<String,HttpSession> sessionMap = new HashMap<>();
    static HttpSessionManger httpSessionManger = null;

    public void addSession(String cookie, HttpSession httpSession){
        sessionMap.put(cookie, httpSession);
    }

    public HttpSession getSession(String cookie){
        return sessionMap.get(cookie);
    }

    private synchronized void removeSession(String cookie){
        sessionMap.remove(cookie);
    }

    public boolean isCheckTimeOut(String cookie){
        HttpSession session = getSession(cookie);
        if (session != null) {
            long dtime = new Date().getTime();
            if (session.getCreateTime() > dtime - 1000 * 60 * ServerProperties.SESSION_TIME_OUT) {
                return false;
            }
        }
        return true;
    }

    public boolean isSession(String cookie){
        if (sessionMap.get(cookie) == null){
            return false;
        }
        return true;
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static HttpSessionManger getInstance() {
        if (httpSessionManger == null) {
            synchronized (RequestUtil.class) {
                if (httpSessionManger == null) {
                    httpSessionManger = new HttpSessionManger();
                }
            }
        }
        return httpSessionManger;
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run(){
            List<String> ids = new ArrayList<>();
            sessionMap.forEach((k,v) ->{
                if (isCheckTimeOut(k)){
                    ids.add(k);
                }
            });
            ids.forEach(key ->{
               removeSession(key);
               logger.debug("sessionManger remove "+ key);
            });
        }
    });
    public HttpSessionManger(){
        scheduledThreadPool.scheduleAtFixedRate(thread, 1, 3, TimeUnit.SECONDS);
    }
}
