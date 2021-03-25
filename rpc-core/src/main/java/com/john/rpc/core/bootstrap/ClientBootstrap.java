package com.john.rpc.core.bootstrap;

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
import io.netty.util.HashedWheelTimer;
import java.util.concurrent.TimeUnit;

public class ClientBootstrap {

    private int port;
    private String host;
    private SocketChannel socketChannel;

    protected final HashedWheelTimer timer = new HashedWheelTimer();

    private Bootstrap boot;


    public ClientBootstrap(int port, String host) throws Exception {
        this.host = host;
        this.port = port;
        start();
    }
    private void start() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        boot = new Bootstrap();
        boot.group(eventLoopGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(new LoggingHandler(LogLevel.INFO));
        ChannelFuture future;
        //进行连接
        try {
            synchronized (boot) {
                boot.handler(new ChannelInitializer<Channel>() {

                    //初始化channel
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new StringDecoder());
                        p.addLast(new StringEncoder());
                        p.addLast(new RpcConsumerHandler());
                    }
                });

                future = boot.connect(host,port);
            }

            // 以下代码在synchronized同步块外面是安全的
            future.sync();
        } catch (Throwable t) {
            throw new Exception("connects to  fails", t);
        }
        if (future.isSuccess()) {
            socketChannel = (SocketChannel) future.channel();
            System.out.println("connect server  success|");
        }
    }


    public int getPort() {
        return this.port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }


    public static void main(String[] args) throws Exception {
        ClientBootstrap bootstrap = new ClientBootstrap(9999, "127.0.0.1");
        while (true){
            Thread.sleep(1000);
            bootstrap.getSocketChannel().writeAndFlush("测试的数据");
        }
    }
}
