package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("todo")
@Slf4j
public class TodoController {

	@Autowired
	private TodoService todoService;
	
	@PostMapping("add")
	public String addTodo(@ModelAttribute Todo todo,
				RedirectAttributes ra
			) {
		//RedirectAttributes : 리다이렉트 시 값을 1회성으로 전달하는 객체
		//RedirectAttributes.addFlashAttribute("key", value) <- 라고 하면, 리다이렉트되는 순간만 세션으로 변경됨.
		
		// [원리]
		// 응답 전 ra의 상태 : request scope
		// redirect 중 : 갑자기 번쩍하고 session scope 가 됨. 
		// 응답 후 : request scope 복귀 
		
		// 서비스 메서드 호출 후 결과 반환 받기
		int result = todoService.addTodo(todo);
		
		// 삽입 결과에 따라 message 값 지정하기
		String message = null;
		
		if(result>0) {
			message = "할 일 추가 성공!!";
		}else {
			message = "할 일 추가 실패 ... ㅠㅠ";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/";
	}
	
	//상세조회
	@GetMapping("detail")
	public String todoDetail(@RequestParam("todoNo") int todoNo, Model model,
			RedirectAttributes ra
			) {
		
		Todo todo = todoService.todoDetail(todoNo);
		
		String path = null;
		
		if(todo != null) { //조회 결과 있을 경우
			// forward : todo/detail
			path = "todo/detail";
			model.addAttribute("todo", todo);

		} else { // 조회 결과 없을 경우
			// redirect : /
			path = "redirect:/";
			//RedirectAttributes :
			// - 리다이렉트 시 데이터를 request scope -> (잠시) session scope 로
			// 전달할 수 있는 객체(응답 후 request scope로 복귀)
			
			ra.addFlashAttribute("message", "해당 할 일이 존재하지 않습니다.");
		

		}
		return path;
	}
	

	/** 완료 여부 변경
	 * @param todo : 커맨드 객체 (@ModelAttribute 생략)
	 * @param ra
	 * @return
	 */
	@GetMapping("changeComplete")
	public String changeComplete(@ModelAttribute Todo todo, RedirectAttributes ra) {
		
		
		//변경 서비스 호출
		int result = todoService.changeComplete(todo);
		
		
		//변경 성공 시 : "변경 성공!!"
		String message = null;
		if(result >0) {
			message = "변경 성공!!";
		}		
		//변경 실패 시 : "변경 실패.." 
		else {
			message = "변경 실패..";
		}

		ra.addFlashAttribute("message", message);
		
		// 현재 요청 주소 : /todo/changeComplete
		// 응답 주소 : /todo/detail	
		return "redirect:/todo/detail?todoNo=" + todo.getTodoNo();
	}
	@GetMapping("/update")
	public String todoUpdate(@RequestParam("todoNo") int todoNo, Model model) {
		
		// 상제 조회 서비스 호출 -> 수정화면에 출력할 이전 내용으로 쓸 것.
		Todo todo = todoService.todoDetail(todoNo);
		model.addAttribute("todo", todo);
		
		return "todo/update";
	}
	
	
	/**할 일 수정
	 * @param todo : 커맨드 객체(@ModelAttribute 로 바인딩한 객체, 뿐만 아니라, 파라미터를 받아 바인딩한 객체를 의미)
	 * @param ra 
	 * @return
	 */
	@PostMapping("/update")
	public String todoUpdate(/*@ModelAttribute*/ Todo todo, RedirectAttributes ra) {
		
		//수정 서비스 호출
		int result = todoService.todoUpdate(todo);
		String path = "redirect:";
		String message = null;
		
		log.info("{}", result);
		
		if(result >0) {
			// 상세 조회로 리다이렉트
			path +=  "/todo/detail?todoNo=" + todo.getTodoNo();
			message = "수정 성공!!!";
		} else {
			path += "/todo/update?todoNo=" + todo.getTodoNo();
			message = "수정 실패..";
		}
		
		log.info("path={}", path);
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
		
	/** 할 일 삭제
	 * @param todoNo : 삭제할 할 일 번호
	 * @param ra
	 * @return 메인페이지/상세페이지
	 */
	@GetMapping("/delete")
	public String todoDelete(@RequestParam("todoNo") int todoNo,
				RedirectAttributes ra
			) {
		
		int result = todoService.todoDelete(todoNo);
		
		String path = "redirect:";
		String message = "";
		
		if(result>0) {
			path += "/";
			message = "성공적으로 삭제되었습니다";
			
		}else {
			path +=  "/todo/detail?todoNo=" + todoNo;
			message = "삭제 중 오류 발생";
		}
		ra.addFlashAttribute("message", message);
		//ra.addFlashAttribute() 의 동작 원리: 
		// 지금 이 순간 세션객체에 데이터를 담는다. 
		// 리다이렉트를 받는 컨트롤러에서 세션객체에 들어있는 데이터를 꺼내서 model 에 담아주고,
		// 그 즉시 세션객체를 삭제해버린다. 
		
		
		return path;
	}
	
	
}
