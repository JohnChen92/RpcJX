package com.john.rpc.core.bootstrap;

import com.john.rpc.core.codec.MessageDecoder;
import com.john.rpc.core.codec.MessageEncoder;
import com.john.rpc.core.handler.RpcConsumerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

public class RpcConsumer {

  public static void main(String[] args) throws Exception {
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      Bootstrap b = new Bootstrap();
      b.group(workerGroup)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15 * 1000)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline ph = ch.pipeline();
              ph.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
              ph.addLast("decoder",new StringDecoder());
              ph.addLast("encoder",new StringEncoder());
              ph.addLast(new RpcConsumerHandler());
            }
          });

      // Start the client.
      ChannelFuture f = b.connect("127.0.0.1", 9988).sync();
      System.out.println(f.channel().isActive());
      Channel channel = f.channel();
      channel.writeAndFlush("rpc//com.john.rpc.core.abc:get?param1=123;param2=345");
      System.out.println("send");
      channel.close();
      // Wait until the connection is closed.
      f.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
    }
  }
}
