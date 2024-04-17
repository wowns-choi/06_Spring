package edu.kh.project.mypage.controller;

import java.io.IOException;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.mypage.model.dto.UploadFile;
import edu.kh.project.mypage.model.service.MyPageService;
import jakarta.mail.Multipart;
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
	//-------------------------------------------------------------------------------
	/*  파일 업로드 테스트 */
	@GetMapping("fileTest")
	public String  fileTest() {
		return "myPage/myPage-fileTest";
	}
	
	/*
	 * 컨트롤러에서 파일을 받을 때에도 규칙이 있음.
	 * Spring 에서 파일 업로드를 처리하는 방법
	 * 
	 * - encType= "multipart/form-data" 인 클라이언트 요청을 컨트롤러가 받으면
	 * 	  문자, 숫자, 파일 등이 섞여있는 요청 일거 아니야?
	 * 	 ->	이를 MultipartResolver 를 이용해서
	 * 		섞여 있는 파라미터를 분리해야함.
	 * 		근데, MultipartResolver 를 빈으로 등록해줘야 하거든? FileConfig 에 정의함.
	 * 	
	 * 		문자열, 숫자 -> String 으로 
	 * 		파일 -> MultipartFile 으로
	 * 		
	 * 		
	 * 
	 * */
	
	/** MultipartFile 이 어떤식으로 동작할지에 대해 FileConfig 에 정의해놓을거임. 
	 * @param memberName 
	 * @param uploadFile
	 * @return
	 */
	@PostMapping("/file/test1")
	public String fileUpload1(		
			@RequestParam("memberName") String memberName,
			@RequestParam("uploadFile") MultipartFile uploadFile,  // MultipartResolver 여기서 열일중
			// 이렇게 받은 uploadFile 에는 업로드한 파일 + 파일에 대한 내용 및 설정 내용이 담겨있음.
			RedirectAttributes ra
			)  throws Exception {
		
		
		String path = service.fileUpload1(uploadFile);			
		log.debug("=======path======{}",path); /*		
																					=======path======/myPage/file/pikachu-02.jpg
																					*/
		// 만약, 파일이 저장되어 웹에서 접근할 수 있는 경로가 반환되었을 때
		if(path != null) {
			ra.addFlashAttribute("path", path);
		}
		
		return "redirect:/myPage/fileTest";
	}
	
	
	
	
	
	
	@PostMapping("file/test2")
	public String fileUpload2(
					@RequestParam("uploadFile") MultipartFile uploadFile,
					@SessionAttribute("loginMember") Member loginMember,
					RedirectAttributes ra
			) throws IOException {
		// 로그인한 회원의 번호 (누가 업로드 했는가)
		int memberNo = loginMember.getMemberNo();
		
		// 업로드된 파일 정보를 DB에 INSERT 후 결과 행의 개수 반환 받을 예정
		int result = service.fileUpload2(uploadFile, memberNo);
		
		String message = null;
		if(result > 0) {
			message = "파일 업로드 성공";
		} else {
			message = "파일 업로드 실패...";
		}
		ra.addFlashAttribute("message", message);
		
		return "redirect:/myPage/fileTest";
		
	}
	
	@GetMapping("/fileList")
	public String fileList(Model model) {
		
		// 파일 목록 조회 서비스 호출
		List<UploadFile> list = service.fileList();
		
		//model list 담아서 
		model.addAttribute("list", list);
		
		// myPage/myPage-fileList.html
		return "/myPage/myPage-fileList";
	}
	
	@PostMapping("file/test3")
	public String fileUpload3(
			@RequestParam("aaa") List<MultipartFile> aaaList,
			@RequestParam("bbb") List<MultipartFile> bbbList,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra
			) throws IOException {
		
		// aaa 파일 미제출 시 
		// -> 0번, 1번 인덱스 파일이 모두 비어있음
		log.debug("================{}", aaaList.get(0));
		log.debug("================{}", aaaList.get(1));

		// bbb(multiple) 파일 미제출 시
		// List의 0번 인덱스 파일이 비어있음. 
		log.debug("================{}", aaaList.get(0));
		log.debug("================{}", aaaList.get(1));
		
		int memberNo = loginMember.getMemberNo();
		
		// result == 업로드 파일 개수
		int result = service.fileUpload3(aaaList, bbbList, memberNo); // 현재 result 에는 업로드된 파일의 개수가 들어있음
		
		String message = null;
		if(result ==0) {
			message = "업로드된 파일이 없습니다.";
			
		}else {
			message = result + "개의 파일이 업로드 되었습니다!";
		}
		
		ra.addFlashAttribute("message" , message);
		
		return "redirect:/myPage/fileTest";
	}
	
	
	@GetMapping("/profile")
	public String method() {
		return "/myPage/myPage-profile";
	}
	
	@PostMapping("/profile")
	public String method2(
			@RequestParam("profileImg") MultipartFile profileImg,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra
			) throws Exception {
		
		// 서비스 호출
		// /myPage/profile/변경된파일명 형태의 문자열
		// 현재 로그인한 회원의 PROFILE_IMG 컬럼값으로 수정(UPDATE)
		int result = service.profile(profileImg, loginMember);
		
		String message = null;
		
		if(result > 0 ) {message = "변경 성공!";}
		else { message = "변경실패 ㅜㅜ";}
		
		ra.addFlashAttribute("message", message);
		
		log.debug("aaaaaaaaaaaaaaaaaaa={}", loginMember.getProfileImg());
		
		return "redirect:profile"; 
		// 상대경로로 작성한것. 
	}
	
}
