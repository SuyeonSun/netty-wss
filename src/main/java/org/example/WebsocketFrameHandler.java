package org.example;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebsocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		if (frame instanceof TextWebSocketFrame) {
			String request = ((TextWebSocketFrame) frame).text();
			System.out.println("Received: " + request);
			ctx.channel().writeAndFlush(new TextWebSocketFrame("Hello, " + request));
		} else {
			throw new UnsupportedOperationException("Unsupported frame type: " + frame.getClass().getName());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
