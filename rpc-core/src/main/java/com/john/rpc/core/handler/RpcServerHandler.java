package com.john.rpc.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.lang.reflect.Method;

public class RpcServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception {
    //基于Http处理
    FullHttpRequest httpRequest = (FullHttpRequest)msg;
    String path = httpRequest.uri();
    String body = getBody(httpRequest);
    HttpMethod method = httpRequest.method();
    HttpHeaders headers = httpRequest.headers();
    System.out.println("path:" + path + "  body:" + body + "  method:" + method);

    //进行方法的调用处理
    Class clazz = Class.forName("com.demo.rpc.server.provider.TestProviderA");
    Method meth = clazz.getMethod("getMsg", String.class);
    Object invoke = meth.invoke(clazz.newInstance(), "123123");

    //Http的返回
    send(ctx,"ok",HttpResponseStatus.OK);
  }

  /**
   * 异常处理
   * @param ctx
   * @param cause
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    // Close the connection when an exception is raised.
    cause.printStackTrace();
    ctx.close();
  }



  /**
   * 获取body参数
   * @param request
   * @return
   */
  private String getBody(FullHttpRequest request){
    ByteBuf buf = request.content();
    return buf.toString(CharsetUtil.UTF_8);
  }

  /**
   * 发送的返回值
   * @param ctx     返回
   * @param context 消息
   * @param status 状态
   */
  private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

}
