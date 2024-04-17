package edu.kh.project.member.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MemberMapper {

	/**로그인 SQL 실행
	 * @param memberEmail
	 * @return
	 */
	Member login(String memberEmail);

	/** 이메일 중복검사
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	int checkNickname(String inputNickname);

	int signup(Member inputMember);

	Member testLogin(String memberEmail);

	List<Member> selectMemberList();

	int updatePwToPass01(Map<String, String> map);

	int updateMemberDelFl(String updateMemberDelFl);
	

	
}
