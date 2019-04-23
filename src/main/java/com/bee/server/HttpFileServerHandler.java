package com.bee.server;
import com.bee.config.Config;
import com.bee.server.http.*;
import com.bee.server.router.Context;
import com.bee.server.router.Routers;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.util.CharsetUtil;
import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.Date;
import java.util.regex.Pattern;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.*;

public class HttpFileServerHandler extends
        SimpleChannelInboundHandler<HttpObject> {
    Routers routers = Routers.getInstance();
    RequestUtil requestUtil = RequestUtil.getInstance();
    HttpSessionManger httpSessionManger = HttpSessionManger.getInstance();

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
        DiskAttribute.baseDirectory = null; // system temp directory
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,  HttpObject httpObject) throws Exception {
        HttpRequest request = (HttpRequest) httpObject;

        /*如果无法解码400*/
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        /*只支持GET POST方法*/
        if (request.method() != GET && request.method() != POST) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

        // 解析http请求 封装contenxt
        Servlet context = requestUtil.parse(httpObject,ctx);

        // 获取指定路由
        Context router = routers.getRouter(context.getRequest().uri());

        // 获取token 生成session 存到session管理中去
        String cookie = context.getRequest().getHeader("Cookie");
        if (cookie != null && Config.OPEN_SESSION) {
            if (!httpSessionManger.isSession(cookie)){
                HttpSession session = new Session();
                session.setCreateTime(new Date().getTime());
                session.setId(cookie);
                httpSessionManger.addSession(cookie,session);
                context.getRequest().setSessionId(cookie);
            } else {
                HttpSession session = httpSessionManger.getSession(cookie);
                if (httpSessionManger.isCheckTimeOut(cookie)) {
                    session.removeAll();
                }
                context.getRequest().setSessionId(cookie);
                session.setId(cookie);
                session.setCreateTime(new Date().getTime());
            }
        }

        if (router == null) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
        } else {
            router.init(context);
            if (!context.checkPrint()) {
                context.print("");
            }
        }

        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        //如果不支持keep-Alive，服务器端主动关闭请求
        if (!HttpUtil.isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private static final Pattern ALLOWED_FILE_NAME = Pattern
            .compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx,
                                  HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                status, Unpooled.copiedBuffer("Failure: " + status.toString()
                + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                mimeTypesMap.getContentType(file.getPath()));
    }
}
