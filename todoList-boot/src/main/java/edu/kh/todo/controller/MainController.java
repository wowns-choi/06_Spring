package edu.kh.todo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import edu.kh.todo.model.service.TodoServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class MainController {

	@Autowired // DI(Dependency Injection) : 의존성 주입
	private TodoService service;
	
	@GetMapping("/")
	public String mainPage(Model model) {
	
		//의존성 주입(DI) 확인
		log.debug(service.toString());
		
		//Service 메서드 호출 후 결과 반환 받기
		Map<String, Object> map = service.selectAll();
		
		//map 에 담긴 내용 추출
	List<Todo> todoList = (List<Todo>) map.get("todoList");
		int count = (int) map.get("completeCount");
		
		log.debug("todoList================={}",  todoList);
		log.debug("count================={}", count);


	model.addAttribute("todoList", todoList);
	model.addAttribute("findCount",count);


	return "common/main";
	}



	
}
