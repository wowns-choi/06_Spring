package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

// Bean : Spring이 만들고 관리하는 객체

@Controller // 요청 // 응답 제어 역할인 Controller 임을 명시 + Bean 등록
public class ExampleController {
	
	// 요청 주소 매핑하는 방법
	
	// 1) @RequestMapping("주소")
	
	// 2) @GetMapping("주소") : Get(조회) 방식 요청 매핑
	
	// 3) @PostMapping("주소") : Post(삽입) 방식 요청 매핑
	
	// 4) @PutMapping("주소") : Put(수정) 방식 요청 매핑
	
	// 5) @DeleteMapping("주소") : Delete(삭제) 방식 요청 매핑
	
	@GetMapping("example")
	public String exampleMethod() {
		
		// forward 하려는 html 파일 경로 작성
		// 단, ViewResolver가 제공하는 
		// Thymeleaf 접두사, 접미사 제외하고 작성
		
		// 접두사 : classpaht:/templates/
		// 접미사 : .html
		return "example"; // src/main/resources/templates/example.html
	}
	

	
}