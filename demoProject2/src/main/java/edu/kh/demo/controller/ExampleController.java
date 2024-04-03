package edu.kh.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.demo.model.dto.Student;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j //lombok 라이브러리가 제공하는 로그 객체 자동생성 어노테이션
@RequestMapping("example")
public class ExampleController {

	@GetMapping("ex1")
	public String example(HttpServletRequest req, Model model) {
		
		/*Model
		 * - Spring 에서 데이터 전달 역할을 하는 객체
		 * 
		 * - org.springframework.ui 패키지
		 * 
		 * - 기본 scope : requests
		 * 
		 * @SessionAttribute 와 함께 사용 시 session scope 로 변환
		 * 
		 * [기본사용법[
		 * model.addAttribute("key", value);
		 * 
		 * */
		
		model.addAttribute("test2", "Model로 전달한 값");
		req.setAttribute("test1", "HttpServletRequest로 전달한 값");
		
		//단일 값(숫자, 문자열) Model 을 이용해서 html 로 전달
		model.addAttribute("productName", "종이컵");
		model.addAttribute("price", 2000);
		
		// 복수값(배열, List) Model 이용해서 html로 전달
		List<String> fruitList = new ArrayList<>();
		fruitList.add("사과");
		fruitList.add("포도");
		fruitList.add("수박");
		model.addAttribute("fruitList",fruitList);
		
		//DTO 객체 Model 을 이용해서 html로 전달
		Student student = new Student();
		student.setStudentNo("1");
		student.setName("wowns");
		student.setAge(31);
		model.addAttribute("student", student);
		
		Student student2 = new Student();
		student2.setStudentNo("2");
		student2.setName("wowns2");
		student2.setAge(32);
		model.addAttribute("student", student2);
		
		Student student3 = new Student();
		student3.setStudentNo("3");
		student3.setName("wowns3");
		student3.setAge(33);
		model.addAttribute("student", student3);
		
		List<Student> stdList = new ArrayList<>();
		stdList.add(student);
		stdList.add(student2);
		stdList.add(student3);
		
		
		
		model.addAttribute("stdList", stdList);
		
		log.info("stdList={}", stdList.toString());

		return "example/ex1"; //templates/example/ex1.html 요청 위임
	}
	
	@PostMapping("/ex2")
	public String ex2(Model model) {
		
		// request -> inputName = "홍길동", inputAge=20, color=[Red, Green, Blue]
		//model.addAttribute("str", "aaa");
	model.addAttribute("str", "<h1> 테스트 중 &times </h1>");
		return "example/ex2";
	}
	
	@GetMapping("/ex3")
	public String ex3(Model model) {
		
		// Model : 데이터 전달용 객체 (request scope)
		model.addAttribute("boardNo", 10);
		
		model.addAttribute("key", "제목");
		model.addAttribute("query", "검색어");
		
		
		return "example/ex3";
	}
	
	/*
	 * @PathVariable 
	 * - 주소 중 일부분을 변수값 처럼 사용
	 *  + 해당 어노테이션으로 얻어온 값은 request scope 에 세팅됨.
	 * 
	 * */
	@GetMapping("/ex3/{number}")
	public String pathVariableTest(@PathVariable("number") int number
														// 주소 중 {number} 부분의 값을 가져와 매개변수에 저장
														// + request scope 에 세팅
			) {
		log.info("======================{}", number);
		return "example/testResult";
	}

	@GetMapping("/ex4")
	public String ex4(Model model) {
		
		Student std = new Student("67890", "잠만보", 22);
	
		model.addAttribute("std", std);
		model.addAttribute("num", 100);
		
		return "example/ex4";
	}
	
	@GetMapping("/ex5")
	public String ex5(Model model) {
		
		// Model : Spring 에서 값 전달 역할을 하는 객체
		// 기본적으로 request scope + session 으로 확장 가능
		
		model.addAttribute("message",  "타임리프 + Javascript 사용 연습");
		model.addAttribute("num1", 12345);
		
		Student std = new Student();
		std.setStudentNo("22222");
		model.addAttribute("std", std);
		
		return "example/ex5";
	}
	
}
