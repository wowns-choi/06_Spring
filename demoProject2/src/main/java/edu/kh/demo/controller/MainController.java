package edu.kh.demo.controller;

//Controller : 요청에 따라 알맞은 서비스 호출 할지 제어
//						서비스 결과에 따라 어떤 응답을 할지 제어

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller // IOC(제어의 역전) 요청/응답 제어 역할 명시 + Bean 등록
@Slf4j
public class MainController {

	
	@RequestMapping("/")
	public String mainPage() {
		
		
		//forward : 요청 위임
		
		//thymeleaf : Spring Boot 에서 사용하는 템플릿 엔진
		
		// thymeleaf를 이용한 html 파일로 forward 시
		// 사용되는 접두사, 접미사가 존재
		
		// 접두사 : classpath:/templates/
		// 접미사 : .html
		// src/main/resources/templates/common/main.html
		
		return "common/main";
	}
}
