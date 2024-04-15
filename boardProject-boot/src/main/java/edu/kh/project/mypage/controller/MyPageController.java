package edu.kh.project.mypage.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.mypage.model.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SessionAttributes({"loginMember"}) // 이거 model 을 세션스코프로 변환할 때에도 써줬었는데, 
										// 꺼내올 때에도 이런식으로 써줘야 함. 
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("myPage")
public class MyPageController {
	
	private final MyPageService service;
	
	/** 내 정보 조회/수정 화면으로 전환
	 * @param loginMember : 세션에 존재하는 loginMember 를 얻어와 매개변수에 대입
	 * @param model : 데이터 전달용 객체(기본 request scope)
	 * @return myPage/myPage-info 로 요청 위임
	 */
	@GetMapping("info")
	public String info(@SessionAttribute("loginMember") Member loginMember //세션스코프에 있는 거 꺼내오는 법 : @SessionAttribute + @SessionAttributes
				, Model model
			) {
		// 주소만 꺼내옴
		String memberAddress = loginMember.getMemberAddress();
		
		// 주소는 필수가 아니었기 때문에, null 일 수 있음. 
		// 따라서, 주소가 있을 경우에만~
		if(memberAddress != null) {
			
			// 구분자 "^^^" 를 기준으로
			// memberAddress 값을 쪼개어 String[] 로 반환
			String[] arr = memberAddress.split("\\^\\^\\^"); // 이스케이프 문자. 왜 이스케이프를 써야 할까? 매개변수로 전달되는 걸 보면, 정규표현식임.
			
			// "01076^^^서울 강북구 수유동 31-67^^^ㅁㅁㅁ"
			// --> (변환) ["01076","서울 강북구 수유동 31-67","ㅁㅁㅁ"]
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
			
		}
		
		return "/myPage/myPage-info";
	}
	
	
	/** 비밀번호 변경 화면 이동
	 * @return
	 */
	@GetMapping("changePw")
	public String changePw() {
		return "myPage/myPage-changePw";
	}
	
	/** 회원 탈퇴 화면 이동
	 * @return
	 */
	@GetMapping("secession")
	public String secession() {
		return "myPage/myPage-secession";
	}
	
	
	/** 프로필 이미지 변경 화면 이동
	 * @return
	 */
	@GetMapping("fileTest")
	public String fileTest() {
		return "myPage/myPage-fileTest";
	}

	/** 회원 정보 수정
	 * @param member : 제출된 회원 닉네임, 전화번호, 주소(,,)
	 * @param loginMember : 로그인한 회원 정보(회원 번호 사용할 예정)
	 * @param memberAddress : 주소만 따로 받은 String[]
	 * @param ra : 리다이렉트시 request scope 로 데이터 전달
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(
				@ModelAttribute Member inputMember,
				@SessionAttribute("loginMember") Member loginMember,
				@RequestParam("memberAddress") String[] memberAddress,
				RedirectAttributes ra
			) {
		
		//inputMember 에 로그인한 회원번호 추가
		int memberNo = loginMember.getMemberNo();
		inputMember.setMemberNo(memberNo);

		// 회원 정보 수정 서비스 호출
		int result = service.updateInfo(inputMember, memberAddress);
		
		String message = null;
		if(result > 0) {
			message = "회원 정보 수정 성공!!";
			
			// loginMember 는 
			// 세션에 저장된 로그인한 회원 정보가 저장된 객체를 참조하고 있다!!
			
			// -> loginMember 를 수정하면
			// 세션에 저장된 로그인한 회원 정보가 수정된다!
			
			// == 세션 데이터와 DB데이터를 맞춤
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
			
		} else {
			message = "회원 정보 수정 실패!!";			
		}
		
		ra.addFlashAttribute(message);

		return "redirect:info"; // 상대경로로 작성해줌. 현재경로의 끝부분만 달라지므로, localhost:/myPage/info 로 get 요청 보내겠지. 
		
	}
	
	/** 비밀번호 변경
	 * @param paramMap : 모든 파라미터를 맵으로 저장
	 * @param loginMember : 세션 로그인한 회원 정보
	 * @param ra 
	 * @return
	 */
	@PostMapping("changePw")
	public String changePw(@RequestParam Map<String, Object> paramMap,
				@SessionAttribute("loginMember") Member loginMember,
				RedirectAttributes ra
			) {
		
		// 로그인한 회원의 번호 얻어오기
		int memberNo = loginMember.getMemberNo();
		
		// 현재 + 새 + 회원번호를 서비스로 전달
		int result = service.changePw(paramMap, memberNo);
		
		String path = null;
		String message = null;
		
		if(result >0) {
			path = "/myPage/info";
			message = "비밀번호가 변경 되었습니다.";
		}else {
			path = "/myPage/changePw";
			message = "현재 비밀번호가 일치하지 않습니다";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}  
	
	/** 회원 탈퇴
	 * @param memberPw : 입력 받은 비밀번호
	 * @param loginMember : 로그인한 회원 정보(세션)
	 * @param status : 세션 완료 용도의 객체
	 * 								-> @SessionAttributes 로 등록된 세션을 완료
	 * @param ra
	 * @return
	 */
	@PostMapping("secession")
	public String secession(@RequestParam("memberPw") String memberPw,
				@SessionAttribute("loginMember") Member loginMember,
				SessionStatus status, 
				
				RedirectAttributes ra
			) {
		
		// 서비스 호출
		int memberNo = loginMember.getMemberNo();
		
		int result = service.secession(memberPw, memberNo);
		
		String message = null;
		String path = null;
		
		if(result >0 ) {
			message = "탈퇴 되었습니다.";
			path = "/";
			
			status.setComplete(); // 세션 완료시킴
			
		}else {
			message = "비밀번호가 일치하지 않습니다.";
			path = "secession";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}
	
	
}
