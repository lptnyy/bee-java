package com.bee.server.http;
import com.bee.config.Config;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.*;
import lombok.var;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.nio.channels.FileChannel;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestUtil {
    private static Logger logger = LogManager.getLogger(RequestUtil.class);
    private HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
    static RequestUtil requestParser;

    /**
     * 单例模式
     *
     * @return
     */
    public static RequestUtil getInstance() {
        if (requestParser == null) {
            synchronized (RequestUtil.class) {
                if (requestParser == null) {
                    requestParser = new RequestUtil();
                }
            }
        }
        return requestParser;
    }


    /**
     * 解析请求参数
     *
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     * @throws IOException
     */
    public Servlet parse(HttpObject fullReq, ChannelHandlerContext chx) throws IOException {
        HttpResponse httpResponse = new BeeHttpResponse();
        com.bee.server.http.HttpRequest httpRequest = new BeeHttpRequest();
        httpRequest.setChx(chx);
        HttpRequest request = (HttpRequest) fullReq;
        HttpMethod method = request.method();
        if (Config.OPEN_SESSION) {
            if (request.headers().get(HttpHeaderNames.COOKIE) == null) {
                HttpCookie httpCookie = new HttpCookie("sessionId", newGUID());
                request.headers().set("Cookie", httpCookie);
            }
        }
        request.headers().forEach(v -> {
            httpRequest.addHeader(v.getKey(), v.getValue());
        });
        if (HttpMethod.GET == method) {
            httpRequest.setMethod("get");
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            decoder.parameters().entrySet().forEach(entry -> {
                // entry.getValue()是一个List, 只取第一个元素
                httpRequest.setParameter(entry.getKey(), entry.getValue().get(0));
            });
            httpRequest.setUri(decoder.uri());
        } else if (HttpMethod.POST == method) {
            httpRequest.setUri(request.uri());
            httpRequest.setMethod("post");
            // 是POST请求
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
            if (decoder != null) {
                if (fullReq instanceof HttpContent) {
                    HttpContent chunk = (HttpContent) request;
                    decoder.offer(chunk);
                    try {
                        while (decoder.hasNext()) {
                            InterfaceHttpData data = decoder.next();
                            if (data != null) {
                                switch (data.getHttpDataType()) {
                                    case Attribute:
                                        Attribute attribute = (Attribute) data;
                                        httpRequest.setParameter(attribute.getName(), attribute.getValue());
                                        break;
                                    case FileUpload:
                                        FileUpload fileUpload = (FileUpload) data;
                                        writeChunk(fileUpload);
                                        break;
                                }
                                data.release();
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    } finally {

                    }
                }
            }
        }
        if (httpRequest.uri().indexOf("?") != -1) {
            httpRequest.setUri(httpRequest.uri().substring(0, httpRequest.uri().lastIndexOf("?")));
        }
        var context = new Servlet();
        httpResponse.setChannelHandlerContext(chx);
        httpResponse.addRequest(httpRequest);
        context.setRequest(httpRequest);
        context.setResponse(httpResponse);
        return context;
    }

    private void writeChunk(FileUpload fileUpload) throws IOException {
        File file = new File("C:\\test\\" + fileUpload.getFilename());
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileChannel inputChannel = new FileInputStream(fileUpload.getFile()).getChannel();
             FileChannel outputChannel = new FileOutputStream(file).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * 产生一个32位的GUID
     * @return
     */
    public static String newGUID()
    {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
