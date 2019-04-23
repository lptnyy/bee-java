package com.bee.server.http;

import io.netty.channel.ChannelHandlerContext;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface HttpRequest {
    public Object getParameter(String name);
    public void setParameter(String name, Object value);
    public List<String> getParameterNames();
    public List<Object> getParameterValues();
    public InputStream getInputStream();
    public void setInputStream(InputStream inputStream);
    public int getInputStreamLength();
    public String uri();
    public String getMethod();
    public void setMethod(String method);
    public void setUri(String uri);
    public Map<String,String> headers();
    public void addHeader(String key, String value);
    public String getHeader(String key);
    public ChannelHandlerContext getChx();
    public void setChx(ChannelHandlerContext chx);
    public HttpSession getSession();
    public void setSessionId(String sessionId);
}
