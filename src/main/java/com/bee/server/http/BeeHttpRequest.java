package com.bee.server.http;

import io.netty.channel.ChannelHandlerContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeeHttpRequest implements HttpRequest{
    HttpSessionManger httpSessionManger = HttpSessionManger.getInstance();
    Map<String,Object> requestMaps = new HashMap<>();
    String method;
    String uri;
    InputStream inputStream;
    Map<String,String> headers = new HashMap<>();
    ChannelHandlerContext chx;
    String sessionId = null;

    @Override
    public Object getParameter(String name) {
        return requestMaps.get(name);
    }

    @Override
    public void setParameter(String name, Object value) {
        requestMaps.put(name,value);
    }

    @Override
    public List<String> getParameterNames() {
        List<String> pars = new ArrayList<>();
        requestMaps.forEach((k,v) ->{
            pars.add(k);
        });
        return pars;
    }

    @Override
    public List<Object> getParameterValues() {
        List<Object> pars = new ArrayList<>();
        requestMaps.forEach((k,v) ->{
            pars.add(v);
        });
        return pars;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int getInputStreamLength() {
        return 0;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public void addHeader(String key, String value) {
        headers.put(key,value);
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public ChannelHandlerContext getChx() {
        return chx;
    }

    @Override
    public void setChx(ChannelHandlerContext chx) {
        this.chx = chx;
    }

    @Override
    public HttpSession getSession() {
        return httpSessionManger.getSession(sessionId);
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
