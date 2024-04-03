package edu.kh.demo.model.dto;

import lombok.Data;

// lombok : 자주 사용하는 코드를 컴파일 시 자동 완성 해주는 라이브러리
// -> DTO(기본생성자, getter/setter, toString) + log

@Data
public class MemberDTO {
	
	private String memberId;
	private String memberPw;
	private String memberName;
	private String memberAge;
	

	
	
}
