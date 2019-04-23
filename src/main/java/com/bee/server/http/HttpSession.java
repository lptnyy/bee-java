package com.bee.server.http;

public interface HttpSession {
    long getCreateTime();
    void setCreateTime(long createTime);
    String getId();
    void setId(String id);
    Object getAttribute(String name);
    void setAttribute(String name,Object value);
    void removeAll();
}
