package org.example;

import java.io.File;

import javax.net.ssl.SSLException;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

public class SecureWebSocketServer {
	public static void main(String[] args) throws SSLException, InterruptedException {
		String homeDir = System.getProperty("user.home");
		// SSL 인증서와 키 파일 경로
		File certFile = new File(homeDir, "cert.pem");
		File keyFile = new File(homeDir, "key-pkcs8.pem");

		// SSLContext 생성
		SslContext sslContext = SslContextBuilder.forServer(certFile, keyFile).build();

		// WebSocket 서버 시작
		new WebSocketServer(sslContext).start();
		System.out.println("Hello world!");
	}
}
