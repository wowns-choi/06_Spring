package edu.kh.project.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// DTO(Data Transfer Object)
// - 데이터 전달용 객체
// - DB에 조회된 결과 또는 SQL 구문에 사용할 값을 전달하는 용도
// - 관련성 있는 데이터를 한 번에 묶어서 다룸.
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder // 인스턴스 쉽게 만들게해줌.
public class Member {
	private Integer memberNo;
	private String memberEmail;
	private String memberPw;
	private String memberNickname;
	private String memberTel;
	private String memberAddress;
	private String profileImg;
	private String enrollDate; //오라클에서는 Date 타입이었으나, String 타입으로 가져올거임. 
	private String memberDelFl;
	private int atuthority; // 관리자인지 여부를 따지는 컬럼임. 
	
	
}
