package edu.kh.project.mypage.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.mypage.model.dto.UploadFile;

public interface MyPageService  {

	/** 회원 정보 수정
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int updateInfo(Member inputMember, String[] memberAddress);

	/** 비밀번호 수정
	 * @param paramMap
	 * @param memberNo
	 * @return
	 */
	int changePw(Map<String, Object> paramMap, int memberNo);

	/** 회원 탈퇴
	 * @param memberPw
	 * @param memberNo
	 * @return result
	 */
	int secession(String memberPw, int memberNo);

	/** 파일 업로드 테스트 1
	 * @param uploadFile
	 * @return
	 */
	String fileUpload1(MultipartFile uploadFile)  throws Exception ;

	int fileUpload2 (MultipartFile uploadFile, int memberNo) throws IOException;

	List<UploadFile> fileList();

	int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo)throws IOException;

	/** 프로필 이미지 변경
	 * @param profileImg
	 * @param loginMember
	 * @return
	 */
	int profile(MultipartFile profileImg, Member loginMember) throws Exception;

}
