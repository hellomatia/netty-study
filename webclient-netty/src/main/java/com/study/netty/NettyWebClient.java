package com.study.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class NettyWebClient {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    public NettyWebClient() {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public CompletableFuture<String> get(String url) {
        CompletableFuture<String> result = new CompletableFuture<>();

        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 80 : uri.getPort();

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new HttpClientCodec());
                    p.addLast(new HttpObjectAggregator(1048576));
                    p.addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                        @Override
                        protected void messageReceived(ChannelHandlerContext ctx, FullHttpResponse fullHttpResponse) throws Exception {
                            ByteBuf content = fullHttpResponse.content();
                            String responseBody = content.toString(StandardCharsets.UTF_8);
                            result.complete(responseBody);
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            result.completeExceptionally(cause);
                            ctx.close();
                        }
                    });
                }
            });

            ChannelFuture f = bootstrap.connect(host, port).sync();
            HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            f.channel().writeAndFlush(request);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            result.completeExceptionally(e);
        } finally {
            group.shutdownGracefully();
        }

        return result;
    }

    public static void main(String[] args) {
        NettyWebClient client = new NettyWebClient();
        client.get("http://localhost:8080")
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
