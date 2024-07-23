package org.example;

import java.util.List;

import javax.net.ssl.SSLException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

public class CustomSslHandler extends SslHandler {
	public CustomSslHandler(javax.net.ssl.SSLEngine engine) {
		super(engine);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("CustomSslHandler added to the pipeline.");
		super.handlerAdded(ctx);
	}

	// user의 channelHandler에서 특정 이벤트가 발생했을 떄 호출된다. ex) SSL 핸드쉐이크
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		System.out.println("Event triggered: " + evt);
		// SslHandshakeCompletionEvent: SSL 핸드쉐이크가 완료될 때 트리거되는 이벤트이다. 이 이벤트는 핸드쉐이크의 성공, 실패 여부를 나타낸다.
		// SSL 핸드쉐이크가 완료되면 SslHandshakeCompletionEvent가 생성된다.
		// SslHandshakeCompletionEvent가 생성되어야 userEventTriggred 메서드가 호출된다.
		if (evt instanceof SslHandshakeCompletionEvent) {
			SslHandshakeCompletionEvent handshakeCompletionEvent = (SslHandshakeCompletionEvent) evt;
			if (handshakeCompletionEvent.isSuccess()) {
				System.out.println("SSL Handshake successful");
			} else {
				System.err.println("SSL Handshake failed: " + handshakeCompletionEvent.cause());
			}
 		}
		super.userEventTriggered(ctx, evt);
	}

	// Netty 자체에는 SSL/TLS 패킷을 직접 디코딩하여 상세 정보를 제공하는 기능이 없습니다.
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws SSLException {
		System.out.println("SSL decode: " + msg);
		super.decode(ctx, msg, out);
	}
}
