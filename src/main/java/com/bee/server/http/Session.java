package com.bee.server.http;

import java.util.HashMap;
import java.util.Map;

public class Session implements HttpSession{
    Map<String,Object> data = new HashMap<>();
    long createTime = 0l;
    String cookie = "";

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getId() {
        return cookie;
    }

    @Override
    public void setId(String id) {
        this.cookie = id;
    }

    @Override
    public Object getAttribute(String name) {
        return data.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        data.put(name,value);
    }

    @Override
    public  void removeAll() {
        data.clear();
    }
}
