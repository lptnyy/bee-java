package com.bee.server.http;

import com.alibaba.fastjson.JSON;
import com.bee.server.HttpServer;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

@Data
public class Servlet {
    private static Logger logger = LogManager.getLogger(HttpServer.class);
    HttpRequest request;
    HttpResponse response;
    boolean checkPrint = false;

    public void print(String value){
        checkPrint = true;
        response.print(value);
    }

    public void printJson(Object object)
    {
        checkPrint = true;
        response.print(JSON.toJSONString(object));
    }
    public HttpSession getSession(){
        return request.getSession();
    }

    public boolean checkPrint(){
        return checkPrint;
    }

    /**
     * 将获得来的参数转成Bean
     * @param sClass
     * @return
     */
    public Object parametersToBean(Class sClass){
        Object object = null;
        try {
            object = sClass.newInstance();
            for(String key: request.getParameterNames()){
                Field filed = null;
                try {
                    filed = sClass.getField(key);
                } catch (NoSuchFieldException e) {
                    logger.debug("Field:"+e);
                    try {
                        filed = sClass.getDeclaredField(key);
                        filed.setAccessible(true);
                    } catch (NoSuchFieldException e1) {
                        logger.debug("DeclaredField(:"+e1);
                    }
                }
                if (filed != null) {
                    filed.set(object, request.getParameter(key));
                }
            }
        } catch (InstantiationException e) {
            logger.error(e);
        } catch (IllegalAccessException e) {
            logger.error(e);
        }
        return object;
    }
}
