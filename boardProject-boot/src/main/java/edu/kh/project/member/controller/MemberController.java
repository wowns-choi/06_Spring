package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/*@SessionAttributes({"key", "key", "key", ...})
 * - Model 에 추가된 속성 중
 * key 값이 일치하는 속성을 session scope로 변경
 * 
 * */

@Controller
@Slf4j
@RequestMapping("member")
@SessionAttributes({"loginMember"})  //@SessionAttributes({"loginMember", "key"... model 에 넣은 키들})
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	/* [로그인]
	 * 	- 특정 사이트에 아이디/비밀번호 등을 입력해서
	 * 		해당 정보가 있으면 조회/서비스 이용
	 * 
	 * 	- 로그인 한 정보를 session 에 기록하여 
	 * 		로그아웃 또는 브라우저 종료시까지
	 * 		해당 정보를 계속 이용할 수 있게 함.
	 * 
	 * */
	/** 로그인
	 * @param inputMember : 커맨드 객체. (memberEmail, memberPw 세팅된 상태)
	 * @param ra : 리다이렉트 시 request 스코프로 데이터를 전달하는 객체. 세션스코프로 변경하면, 리다이렉트를 받는 컨트롤러에서 꺼내서 model 에 담고,  지워버림.
	 * @param model :데이터 전달용 객체(기본 request scope)
	 * @return "/redirect:/"
	 */
	@PostMapping("login")
	public String login(@ModelAttribute Member inputMember, RedirectAttributes ra, Model model) { 
		
		Member loginMember = service.login(inputMember);
		
		// 로그인 실패 시
		if(loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다. ");

		} 
	
		// 로그인 성공 시 
		if(loginMember != null) {
			//Session scope 에 loginMember 추가
			// 1단계 : 일단 Model 에 담아
			model.addAttribute("loginMember", loginMember); //request 스코프에 올라진것.
			// 이걸 어떻게 세션 스코프에 담을까? 
			// 2단계 : @SessionAttributes({"loginMember"}) 클래스레벨에 이거 써주면 됨. 
			
			
		}

	
		return "redirect:/"; // 메인페이지 재요청
	}
	
	
	
}
