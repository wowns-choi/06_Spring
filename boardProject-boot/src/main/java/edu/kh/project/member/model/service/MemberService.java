package edu.kh.project.member.model.service;

import java.util.List;

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

	Member testLogin(String memberEmail);

	/** 빠른 로그인
	 * @param memberEmail
	 * @return loginMember
	 */
	Member quickLogin(String memberEmail);

	List<Member> selectMemberList();

	int updatePwToPass01(String inputMemberNo);

	int updateMemberDelFl(String updateMemberDelFl);

	
}
