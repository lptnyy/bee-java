package com.bee.server.http;

import com.bee.config.Config;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class BeeHttpResponse implements HttpResponse {
    ChannelHandlerContext channelHandlerContext;
    HttpRequest httpRequest;

    @Override
    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public void addRequest(HttpRequest request) {
        this.httpRequest = request;
    }

    @Override
    public HttpRequest getRequest() {
        return httpRequest;
    }

    @Override
    public void print(String value) {
        FullHttpResponse response = null;
        byte[] datas = new byte[0];
        try {
            datas = value.getBytes(Config.CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(datas));
        response.headers().set(CONTENT_TYPE, "text/html");
        response.headers().set(CONTENT_LENGTH,response.content().readableBytes());
        response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        if (Config.OPEN_SESSION) {
            String cookie = httpRequest.getHeader("Cookie").split("=")[1].replace("\"", "");
            HttpCookie httpCookie = new HttpCookie("sessionId", cookie);
            response.headers().set(SET_COOKIE, httpCookie);
        }
        channelHandlerContext.write(response).addListener(ChannelFutureListener.CLOSE);
        channelHandlerContext.flush();
    }
}
