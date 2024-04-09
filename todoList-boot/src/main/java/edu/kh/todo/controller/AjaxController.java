package edu.kh.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

/* @ResponseBody
 * - 컨트롤러 메서드의 반환값을 
 * 	  HTTP 응답 본문에 직접 바인딩하는 역할임을 명시
 * 	  
 * 	- 컨트롤러 메서드의 반환값을 
 * 		비동기 요청했던 HTML/JS 파일 부분에
 * 		값을 돌려보낼것이다 명시
 * 
 * -  forward/redirect 로 인식하지 않는다. 
 * 
 * 
 * @RequestBody
 * - 비동기 요청(ajax) 시 전달되는 데이터 중
 * 		body 부분에 포함된 요청 데이터를 
 * 		알맞은 Java 객체 타입으로 바인딩하는 어노테이션
 * 
 * - 비동기 요청 시 body 에 담긴 값을
 * 		알맞은 타입으로 변환해서 매개변수에 저장
 * 
 *  [HttpMessageConverter]
 * Spring 에서 비동기 통신 시 
 * - 전달되는 데이터의 자료형 
 * - 응답하는 데이터의 자료형
 * 위 두 가지를 알맞은 형태로 가공(변환) 해주는 객체
 * 
 * - 문자열, 숫자 <-> TEXT
 * - DTO <-> JSON 
 * ****************** - MAP <-> JSON **************** DTO 만들기 싫을 경우 MAP 자료구조에  담아서 JS에 보내줄 수 있음.  
 * 
 * (참고)
 * HttpMessageConverter 가 동작하기 위해서는
 * Jackson-data-bind 라이브러리가 필요한데, 
 * Spring Boot 모듈에 내장되어 있음
 * (Jackson : 자바에서 JSON 다루는 방법 제공하는 라이브러리)
 * 따라서, 레거시를 쓰려는 경우, Jackson 라이브러리 추가해줘야 함. 
 * 
 * */


@Slf4j
@Controller
@RequestMapping("ajax") // 요청 / 응답 제어 역할 명시 + Bean 등록
public class AjaxController {
	
	// @Autowired 
	// - 등록된 Bean 중 같은 타입 또는 상속관계인 Bean 을
	// 해당 필드에 의존성 주입(DI)
	
	@Autowired
	private TodoService service;
	
	@GetMapping("main") //ajax/main GET 요청 매핑
	public String ajaxMain() {
		// 접두사 : classpath:templates/
		// 접미사 : .html
		return "ajax/main";
	}
	
	
	/** 전체 Todo 개수 조회
	 * 
	 */
	@GetMapping("totalCount")
	@ResponseBody
	public int getTotalCount() {
		// 전체 할 일 개수 조회 서비스 호출 및 응답
		int totalCount = service.getTotalCount();
		

		
		return totalCount;
	}
	
	@GetMapping("completeCount")
	@ResponseBody
	public int getCompleteCount() {
		int result = service.getCompleteCount();	
		log.info("result={}",result);
		
		return result;
	}
	
	@PostMapping("add") 
	@ResponseBody // 비동기 요청 결과로 값 자체를 반환
	public int addTodo(@RequestBody Todo todo //요청 body에 담긴 값을 Todo에 저장
			) {
		log.debug(todo.toString());
		int result = service.addTodo(todo);
		log.info("================{}",result);
		return result;
	}
	
	@GetMapping("selectList")
	@ResponseBody
	public List<Todo> selectList() {
		
		List<Todo> todoList = service.selectList();
		return todoList;		
		
		//List(Java 전용 타입) 를 반환
		// -> JS 가 인식할 수 없기 때문에
		// HttpMessageConverter 가 JSON 으로 자동으로 변환해줌. 
		// 즉, List 자료구조를 @ResponseBody 로 내보내면, JSON 객체로 내보내준다. 
		// -> [{}, {}, {}] JSONArray
	}
	
	
	@GetMapping("detail")
	@ResponseBody
	public Todo selectTodo(@RequestParam("todoNo") int todoNo) { // ajax 여도, get요청보내면서 보내준 쿼리스트링은 @RequestParam 으로 받을 수 있다. 
		return service.todoDetail(todoNo);
		//	return 자료형 : Todo
		// -> HttpMessageConverter 가 String(JSON) 형태로 변환해서 반환
	}
	
	@ResponseBody
	@DeleteMapping("delete") // DELETE/PUT 은 비동기방식으로만 통신할 때 사용함. 
	public int deleteTodo( @RequestBody int todoNo
			//@RequestBody Todo todo  : 이렇게 Todo 라는 DTO 에 바로 바인딩 할 수 없는 이유는, HTTP요청 본문이 json 형태의 문자열이 아니라, 단순히 하나의 값이 예를 들어 123 이렇게 왔기 때문이다. 
			// @RequestBody 를 이용해서 자동으로 @ModelAttribute 처럼 객체에 바인딩 되려면, HTTP 요청 메세지 본문이 json 형태의 문자열이라는 것을 기억해야 한다. 
			
			) {
		
		log.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		return service.todoDelete(todoNo);
	}
	
	// Rest API 에 대하여. 
	// Restful 한 API 를 가리킨다. 
	// 자원중심의 이야기
	// HTTP 메서드
	// - GET : 자원 조회
	// - POST : 자원 생성
	// - PUT : 자원 업데이트
	// - DELETE : 자원 삭제 
	
	//REST API 에서 중점적으로 봐야 할 것 중에 한 가지가 더 있는데, 
	// URL 임. 
	// INSERT 하는 경우, add 라고만 해야함. 만약, todo-add 이런식으로 지맘대로 todo- 이딴거 붙이면 
	// RESTFUL 하지 않다고 함. 
	
	// 전체적인 느낌은, 일종의 HTTP 통신할 때의 지켜야 할 규칙 정도?
	
	@ResponseBody
	@PutMapping("/changeComplete")
	public int changeComplete(@RequestBody Todo todo) {
		return service.changeComplete(todo);
	}
	
	//할일 수정
	@PutMapping("/update")
	@ResponseBody
	public int todoUpdate(@RequestBody Todo todo) {
		return service.todoUpdate(todo);
	}
	
	
	
}
