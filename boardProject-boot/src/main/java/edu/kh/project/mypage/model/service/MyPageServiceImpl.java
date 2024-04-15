package edu.kh.project.mypage.model.service;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.mypage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor=Exception.class) // rollbackFor = Exception.class : 모든 예외 발생 시 롤백하겠다는 의미. 
@Slf4j																		//rollbackFor 안쓰면 런타임예외시에만 롤백함.
public class MyPageServiceImpl implements MyPageService{
	
	private final MyPageMapper mapper;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	

	/** 회원 정보 수정
	 */
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		// 입력된 주소가 있을 경우
		// memberAddress 를 A^^^B^^^C 형태로 가공
		
		// 주소 입력 X -> inputMember.getMemberAddress() -> ",,"
		if(inputMember.getMemberAddress().equals(",,")) {
			// 주소에 null 대입
			inputMember.setMemberAddress(null);
			
		} else {
			// memberAddress 를 A^^^B^^^C 형태로 가공
			String address = String.join("^^^", memberAddress);
			
			// 주소에 가공된 데이터를 대입
			inputMember.setMemberAddress(address);
			
			
		}
		
		// SQL 수행 후 결과 반환
		return mapper.updateInfo(inputMember);
		
	}

	@Override
	public int changePw(Map<String, Object> paramMap, int memberNo) {

		// 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회
		String originPw = mapper.selectPw(memberNo); 
		
		//입력받은 현재 비밀번호와 (평문)
		// DB에서 조회한 비밀번호 비교 (암호화)
		// BCryptPasswordEncoder.matches(평문, 암호화된 비밀번호);
			
		// 사용자가 현재 비밀번호라고 입력한 값과 진짜 현재 비밀번호가 다를 경우
		if(!bCryptPasswordEncoder.matches((String)paramMap.get("currentPw"), originPw)) {
			return 0;
		} 
		
		// 같을 경우
		// 새 비밀번호를 암호화 진행
		String encPw = bCryptPasswordEncoder.encode((String) paramMap.get("newPw"));
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo);
		
		return mapper.changePw(paramMap);
		
	}

	/** 회원탈퇴
	 */
	@Override
	public int secession(String memberPw, int memberNo) {
		
		String originPw = mapper.selectPw(memberNo);
		
		
		if(!bCryptPasswordEncoder.matches(memberPw, originPw)) {
			return 0;
		}
		return mapper.secession(memberNo);

	}
	

}
