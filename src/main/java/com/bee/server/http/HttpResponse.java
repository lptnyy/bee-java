package com.bee.server.http;

import io.netty.channel.ChannelHandlerContext;

public interface HttpResponse {
    void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext);
    void addRequest(HttpRequest request);
    HttpRequest getRequest();
    public void print(String value);
}
