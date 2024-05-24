package edu.kh.project.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import edu.kh.project.websocket.handler.ChattingWebsocketHandler;
import edu.kh.project.websocket.handler.TestWebsocketHandler;
import lombok.RequiredArgsConstructor;

@Configuration // 서버 실행 시 작성된 메서드를 모두 수행시켜주는 어노테이션 
@EnableWebSocket // 웹소켓 활성화 설정 
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer{
	
	//Bean 으로 등록된 SessionHandShakeInterceptor
	private final HandshakeInterceptor handshakeInterceptor;
	
	// 웹소켓 처리 동작이 작성된 객체 의존성 주입 
	private final TestWebsocketHandler testWebsocketHandler; 
	
	// 채팅관련 웹소켓 처리 동작이 작성된 객체 의존성 주입 
	private final ChattingWebsocketHandler chattingWebsocketHandler;
	
	// 웹소켓 핸들러를 등록하는 메서드 
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry.addHandler(testWebsocketHandler, "/testSock")
		// ws://localhost/testSock 으로 
		// 클라이언트가 요청을 하면 
		// testWebsocketHandelr가 처리하도록 등록 
		.addInterceptors(handshakeInterceptor)
		// 클라이언트 연결 시 HttpSession 을 가로채 핸드러에게 전달 
		
		.setAllowedOriginPatterns(
				"http://localhost/", 
				"http://127.0.0.1", 
				"http://192.168.50.204")
		// 웹소켓 요청이 허용되는 ip/도메인 지정
		
		.withSockJS(); //SockJs 지원 
		
		registry.addHandler(chattingWebsocketHandler, "/chattingSock")
				.addInterceptors(handshakeInterceptor)
				.setAllowedOriginPatterns(
						"http://localhost/", 
						"http://127.0.0.1", 
						"http://192.168.50.204")
				.withSockJS();
		
		
		
	}

	
}
