package com.john.rpc.core.bootstrap;

import com.john.rpc.core.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class RpcProvider {

  private int port;

  public RpcProvider(int port) {
    this.port = port;
  }

  public void run() {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline ph = ch.pipeline();
              ph.addLast("encoder",new HttpResponseEncoder());
              ph.addLast("decoder",new HttpRequestDecoder());
              ph.addLast("aggregator", new HttpObjectAggregator(10*1024*1024));//把单个http请求转为FullHttpReuest或FullHttpResponse
              ph.addLast("handler", new RpcServerHandler());// 服务端业务逻辑
            }
          });
//          .option(ChannelOption.SO_BACKLOG, 128)
//          .childOption(ChannelOption.SO_KEEPALIVE, true);

      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(port).sync();
      System.out.println("服务端启动成功...");

      f.channel().closeFuture().sync();
      System.out.println("服务端关闭...");

    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) {
    RpcProvider server = new RpcProvider(8080);
    server.run();
  }

}
