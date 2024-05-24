package edu.kh.project.main.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PropertySource("classpath:/config.properties")
public class MainController {
	
	@Value("${my.public.data.service.key.decode}")
	private String decodeServiceKey;
	
	@RequestMapping("/") // "/" 요청 매핑 (method 가리지 않음)
	public String mainPage  () {
		return "common/main";
	}
	
	/* 공공데이터 서비스키 리턴하기 */
	@GetMapping("/getServiceKey")
	@ResponseBody
	public String getServiceKey() {
		return decodeServiceKey;
	}
	
}
