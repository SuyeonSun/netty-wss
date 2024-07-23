package org.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
	private final SslContext sslContext;

	public WebSocketServer(SslContext sslContext) {
		this.sslContext = sslContext;
	}

	public void start() throws InterruptedException {
		// EventLoopGroup: EventLoop(I/O 작업을 처리하는 쓰레드로 구성된 루프)를 처리하는 쓰레드로 구성된 루프
		// NioEventLoopGroup: EventLoopGroup의 구현체 중 하나로, Java NIO를 사용하여 비동기 I/O 작업 처리
		// bossGroup에서 받은 클라이언트의 비동기 요청을 workerGroup으로 전달
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			// ServerBootStrap은 Netty에서 서버 어플리케이션을 bootstrap하고 구성하는데 사용되는 클래스
			// bootstrap이란, 서버를 시작하기 위해 필요한 초기 설정을 의미
			// 1. EventLoopGroup 설정: 서버의 bossGroup과 workerGroup을 설정하여 연결 요청과 I/O 작업 처리
			// 2. ServerBootStrap 설정:
			// - group(boss, worker)인 EventLoopGroup을 설정하여 서버의 이벤트 루프 구성
			// - channel(NioServerSocketChannel.class): 서버 소켓 채널 클래스 설정 (NIO 기반의 서버 소켓 사용)
			// - childHandler: 각 새 연결에 대해 Channel의 초기화 작업 수행
			// 3. ChannelPipeline 설정: 서버가 사용할 Channel 설정 (일반적으로 NioServerSocketChannel을 생성하여, NIO 기반의 비동기 서버 소켓을 설정)
			// - SSL 핸들러 추가, HTTP 요청과 응답 인코딩 및 디코딩, 웹소켓 프로토콜 처리하는 핸들러 추가
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						if (sslContext != null) {
							pipeline.addLast(sslContext.newHandler(ch.alloc())); // SSL 핸들러 추가하여 보안 연결 담당
						}
						pipeline.addLast(new HttpServerCodec()); // HTTP 요청 인코딩 및 디코딩
						pipeline.addLast(new HttpObjectAggregator(65536));
						pipeline.addLast(new ChunkedWriteHandler());
						pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
						pipeline.addLast(new WebsocketFrameHandler());
					}
				});

			Channel ch = b.bind(9000).sync().channel();
			System.out.println("WebSocket Server started at port 9000");
			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
