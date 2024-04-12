package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	public String login(@ModelAttribute Member inputMember, RedirectAttributes ra, Model model, @RequestParam(value="saveId", required=false) String saveId,
				HttpServletResponse response
			) { 
		
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
			
			
			//아이디 저장을 위한 코드 (쿠키를 이용해서) import jakarta.servlet.http.Cookie
			
			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
			//saveId=user01@kh.or.kr
			
			// 클라이언트가 어떤 요청을 할 때 쿠키가 첨부될지 지정
			
			// ex) "/" : IP 또는 도메인 또는 localhost 라는 뜻이거든? 이를 메인페이지라고 해보자.
			//	그래서, cookie.setPath("/"); 라고 하면,  브라우저가 메인 페이지 + 그 하위 주소 를 url 로 하는 HTTP요청을 하면 이 쿠키를 보내주도록 한다.   
			cookie.setPath("/"); //  "/" 라는 url 로 브라우저가 HTTP요청을 할 때에만 브라우저에
			
			//만료 기간 지정
			log.debug(saveId); // 체크박스인데, 체크된 경우 on 을 안 된 경우 null 이 들어옴.
			if(saveId != null) {
				// 아이디 저장 체크 된 경우
				cookie.setMaxAge( 60 * 60 * 24 * 30 ); // 초 단위 
				
			} else {
				// 아이디 저장 미체크 된 경우
				cookie.setMaxAge(0); // 쿠키를 만료시키는 방법은 만료 기간을 0초로 설정하는 것임.
			}
			
			//응답 객체에 쿠키 추가 -> 클라이언트로 전달
			response.addCookie(cookie);
		}

		return "redirect:/"; // 메인페이지 재요청
	}
	
	/** 로그아웃 : Session 에 저장된 로그인된 회원 정보를 없앰(만료, 무효화)
	 * @param SessionStatus : 세션을 완료(없앰) 시키는 역할의 객체
	 * 					- @SessionAttribute({어쩌구}) 라고 하면, model 에 추가된 걸 세션스코프에 올리는거였잖아. 
	 * 					- @SessionAttribute() 로 설정해준 세션객체를 만료시켜주는 역할을 하는 타입의 객체가 SessionStatus 이다. 
	 * 					- 서버에서 기존 세션 객체가 사라짐과 동시에
	 * 						새로운 세션 객체가 생성되어 클라이언트와 연결
	 * 						즉, SessionStatus 를 이용하면 세션을 죽이고, 새로운 세션을 만들어준다고.
	 * @return
	 * 
	 * !-- 추가 정보 --!
	 * 원래 세션스코프에 있는 데이터 지우려면, HttpServletRequest 타입 객체에 대고 .invalide() 메서드를 호출
	 * 하면 됬었잖아. 근데, @SessionAttribute 로 model 에 담아준 걸 세션스코프에 올렸을 때,
	 * 이 세션객체를 지우려면 .invalide() 메서드를 호출하는 것만으로는 부족하고, 
	 * @SessionAttribute 를 이용해서 세션객체를 삭제해줘야 함. 
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		status.setComplete(); //세션을 만료시킴
			return "redirect:/";
	}
	
	/** 회원 가입 페이지로 이동
	 * @return
	 */
	@GetMapping("signup")
	public String signUpPage() {
		return "member/signup";
	}
	
	@ResponseBody // 응답 본문(요청한 fetch) 으로 돌려보냄.
	@GetMapping("checkEmail")
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		// db 에 동일한 이메일이 있는지 검증
		return service.checkEmail(memberEmail);
	}
	
	@ResponseBody
	@GetMapping("/checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
				
		return service.checkNickname(memberNickname);
	}
	
	
	/** 회원 가입
	 * @param member : 입력된 회원정보 (memberEmail, memberPw, memberNickname, memberTel, memberAddress - 따로 받아서 처리)
	 * @param memberAddress : 입력한 주소 input 3개의 값을 배열로 전달[우편번호, 도로명/진버주소, 상세주소]
	 * @param ra : 리다이렉트시 requst scope 로 데이터 전달하는 객체
	 * @return
	 */
	@PostMapping("signup")
	public String signup(@ModelAttribute Member member, 
			@RequestParam("memberAddress") String[] memberAddress,
			RedirectAttributes ra) {
		log.debug("bbbbbbbbbb={}", member.getMemberAddress());
		

		log.debug("aaaaaaaaaaaaaa={}", memberAddress[1]);
		log.debug("aaaaaaaaaaaaaa={}", memberAddress[2]);
		log.debug("aaaaaaaaaaaaaa={}", memberAddress[0]);
		
		// 회원 가입 서비스 호출		
		int result = service.signup(member, memberAddress);

		String path = null;
		String message = null;
		 
		if(result > 0) {
			// 성공 시
			message = member.getMemberNickname() + "님의 가입을 환영 합니다.";
			path = "/";
			
		} else {
			// 실패 시
			message = "회원가입 실패..";
			path = "signup";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		
	}

	
	
	
	
}
