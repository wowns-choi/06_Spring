package edu.kh.project.common.exception;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/* 스프링 예외 처리 방법(우선 순위별로 작성)
 * 
 * 1. 메서드에서 직접 처리 (try-catch, throws)
 * 
 * 2. 컨트롤러 클래스에서 클래스 단위로 모아서 처리 
 * ( @ExceptionHandler 어노테이션을 지닌 메서드를 작성 )
 * 
 * 3. 별도 클래스를 만들어 프로젝트 단위로 모아서 처리 <- 오늘 할거 
 * ( @ControllerAdvice 어노테이션을 지닌 클래스를 작성 )
 * 
 * 
 * */

// @ControllerAdvice
// - 전역적 예외 처리 

@ControllerAdvice
public class ExceptionController {
	
	// 아래 notFound 메서드가 404에러를 받아줄 메서드라는걸 알려줘야 함. 
	// @ExceptionHandler(예외 종류)
	// 여기서 예외 종류는 다음과 같은 것들을 쓸 수 있음. 
	// 1) @ExceptionHandler(SQLException.class) - SQL 관련 예외만 처리
	// 2) @ExceptionHandler(IOException.class) - 입출력 관련 예외만 처리
	// 3) @ExceptionHandler(Exception.class) - 모든 예외 처리 
	@ExceptionHandler(NoResourceFoundException.class) // NoResourceFoundException 은 자원을 찾을수 없을 때 나는 예외임. 
	public String notFound() { 
		return "error/404";
	}
	
	// 예외 처리하는 메서드에서 사용가능한 매개변수는 컨트롤러 내 메서드에서 사용하는 모든 매개변수 작성 가능 
	@ExceptionHandler(Exception.class)
	public String allExceptionHandler(Exception e, Model model) {
		e.printStackTrace();
		model.addAttribute("e", e);
		return "error/500";
	}
	

	
	
}
