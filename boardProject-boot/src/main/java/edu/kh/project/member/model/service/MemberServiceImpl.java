package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service // 비즈니스 로직 처리 역할 명시 + Bean 등록
@Transactional(rollbackFor = Exception.class) // 해당 클래스 메서드 종료 시 까지 
							// - 디폴트 : 런타임 예외가 발생하지 않으면 commit, 런타임 예외가 발생하면 rollback
							// - (rollbackFor = Exception.class)  : 붙여주면, 체크예외가 발생하지 않으면 commit, 체크예외가 발생하면 rollback

public class MemberServiceImpl implements MemberService{
	
	// 등록된 bean 중에서 같은 타입 또는 상속관계인 bean을
	// 자동으로 의존성 주입(DI)
	@Autowired
	private MemberMapper mapper;
	
	// BCrypt 암호화 객체 의존성 주입 (SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	//로그인 서비스
	@Override
	public Member login(Member inputMember) {
		
		// memberEamil : user01@kh.or.kr
		// memberPw : pass01!
		
		// loginMember.getMemberPw() 라고 하면, 평문이 나오지
		// 근데, DB 에는 현재 암호화된 비밀번호가 들어가 있음. 
		
		//테스트 
		
		// bCryptPasswordEncoder.encode(평문) : 평문에 소금쳐짐
//		String str = bCryptPasswordEncoder.encode(inputMember.getMemberPw());		
//		log.info("여기요여기=={}", str); //pass01! : $2a$10$5us2BBqaWvnA8qlG1y7PwuNGMEGS26c4N1wFQKpubP2q1y88IlL.i
//		
//		boolean result = bCryptPasswordEncoder.matches(inputMember.getMemberPw(), str );
//		
//		log.debug("result = {}", result);
		
		// 1. 이메일이 일치하면서 탈퇴하지 않은 회원 조회
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		// 2. 만약에 일치하는 이메일이 없어서 조회 결과가 null 인 경우
		if(loginMember == null) {
			//이메일이 일치하지 않는 경우
			return null;
		}
		
		// 3. 입력 받은 비밀번호(inputMember.getMemberPw)
		// 암호화된 비밀번호 (loginMember.getMemberPw())
		
		//비밀번호가 일치하지 않는 경우
		if(!bCryptPasswordEncoder.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}
		
		// 세션객체에 담아줄건데, 세션객체가 서버에서 관리된다고 하더라도, 
		// 세션객체에 비밀번호가 있으면 해킹위험이 아무래도 있겠지?
		// 그래서, 리턴할 Member 타입 객체의 비밀번호를 삭제해줄거임. 
		inputMember.setMemberPw(null);
		
		
		return loginMember;
	}

	//이메일 중복 검사
	@Override
	public int checkEmail(String memberEmail) {
		return mapper.checkEmail(memberEmail);
	}
	
	
}

/* BCrypt 암호화 (Spring Security 에서 제공)
 *  
 *  - 입력된 문자열(비밀번호) 에 salt(소금)를 추가한 후 암호화
 *  	왜 소금이라고 표현할까? 
 *  	요리할 때, 소금을 항상 한 알갱이 씩 정량할 수 없잖아. 
 *  	이처럼, 암호화할 때, 매번 같은 양으로 소금(원본에 가해질 뒤틀림)칠 수 없다.
 *  	
 *   ex) A 회원 : 1234 -> $12!asdfg
 *   ex) B 회원 : 1234 -> $12!dfasd	
 *  
 *  - 비밀번호 확인 방법
 *  -> BCryptPasswordEncoder.matches(평문 비밀번호, 암호화된 비밀번호) 
 *  	라고 해주면 boolean 값으로 두 비밀번호가 같은지 여부로 알려준다.  
 *  
 *  * 로그인 / 비밀번호 변경 / 탈퇴 등 비밀번호가 입력되는 경우
 *  	DB에 저장된 암호화된 비밀번호를 조회해서
 *  	matches() 메서드로 비교해야 한다!
 *  
 *  
 *  ---------------------------------------------------------------------------------
 *  (참고)
 *  예전에는 BCrpyt 암호화 방식 말고, sha 방식 암호화기법을 사용함.  
 *   ex) A 회원 : 1234 -> $12!abcd
 *   ex) B 회원 : 1234 -> $12!abcd	
 * 	 이게 sha 방식인데, sha방식은 항상 똑같은 양을 소금을 침. 
 *  그렇다고 해서 안정성이 떨어지는 건 아니지만, BCrypt 방식이 더 안정성이 높은게 맞지.
 * 
 * */
