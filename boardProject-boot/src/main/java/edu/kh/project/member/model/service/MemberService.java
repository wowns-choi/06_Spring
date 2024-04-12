package edu.kh.project.member.model.service;

import edu.kh.project.member.model.dto.Member;

public interface MemberService {

	/** 로그인 
	 * @param loginMember
	 * @return
	 */
	Member login(Member loginMember);

	/** 이메일 중복검사 서비스
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	int checkNickname(String inputNickname);

	/** 회원 가입 서비스
	 * @param member
	 * @param memberAddress
	 * @return result
	 */
	int signup(Member member, String[] memberAddress);
	
}
