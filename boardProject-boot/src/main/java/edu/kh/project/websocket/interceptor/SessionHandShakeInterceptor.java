package edu.kh.project.websocket.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;

@Component // bean 등록 
public class SessionHandShakeInterceptor implements HandshakeInterceptor{

	// 핸 들러 동작 전에 수행되는 메서드 
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		//ServerHttpRequest : HttpServletRequest 의 부모 인터페이스
		//ServerHttpResponse : HttpServletResponse 의 부모 인터페이스
		
		// attributes : 해당 맵에 세팅된 속성 (데이터)
		//              다음에 동작할 Handler 객체에게 전달됨
		
		// request 가 참조하는 객체가 
		// ServletServerHttpRequest 로 다운캐스팅이 가능한가? 
		if(request instanceof ServletServerHttpRequest) {
			
			//다운캐스팅
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			
			// 웹소켓 동작을 요청한 클라이언트의 세션을 얻어옴. 
			HttpSession session = servletRequest.getServletRequest().getSession();
			
			attributes.put("session", session);
		}
		
		//true 로 꼭 바꿔줘야함. 가로채기 진행 여부 true -> true 로 작성해야 세션을 가로채서 Handler에게 전달 가능
		return true;
	
	}

	// 
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
		
	}
	
}
