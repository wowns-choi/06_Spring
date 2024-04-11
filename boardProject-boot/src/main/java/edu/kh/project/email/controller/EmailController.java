package edu.kh.project.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.email.model.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("email")
@RequiredArgsConstructor // final 필드 / @NotNull 필드에 자동으로 의존성 주입 (@Autowired  생성자 방식 코드 자동완성)
public class EmailController {
	// 이메일 컨트롤러를 따로 만든 이유 : 이메일 기능을 프로젝트 여러 곳에서 이용하고 싶을 수 있으니까. 
	// 근데, 서비스계층에서 하면 되잖아.
	

	private final EmailService service;
	
	@PostMapping("signup")
	@ResponseBody
	public int sendEmail (@RequestBody String email) {
		
		String authKey = service.sendEamil("signup",email);
		
		return 0;
	}
}

/* @Autowried 를 이용한 의존성 주입 방법은 총 3가지가 존재한다. 
 *  1) 필드 
 *  2) setter
 *  3) 생성자 (권장)
 * 
 *	근데, 의존성 주입하려고 생성자 만들기 너무 귀찮.
 * 그래서, Lombok 라이브러리 에서 제공하는 
 * @RequiredArgsConstructor 를 이용하면
 * 
 * 필드 중 
 * 1) 초기화 되지 않은 final 이 붙은 필드
 * 2) 초기화 되지 않은 @NotNull 이 붙은 필드 : @NotNull 이 붙은 필드 는 초기화되지 않으면 안되는 필드.
 * 
 * 1, 2 에 해당하는 필드에 대한
 * @Autowired 생성자 구문을 자동 완성
 * 
 * 
 * */

// 1) 필드에 의존성 주입하는 방법 (권장되는 방법 아니다)
// @Autowired
// private EmailService service;

// 2) setter 를 이용한 방법
//private EmailService service; 
//@Autowired
// public void setService(EmailService service){
//		this.service = service;
//}

// 3) 생성자를 이용한 방법
// private EmailService service;
// private MemberService service2;
//@Autowired
// public EmailController(EmailService service, MemberService service2){
// this.service = service;
//	this.service2 = service2;
// }
