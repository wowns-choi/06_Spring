package edu.kh.project.mypage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.mypage.model.dto.UploadFile;
import edu.kh.project.mypage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor=Exception.class) // rollbackFor = Exception.class : 모든 예외 발생 시 롤백하겠다는 의미. 
@Slf4j																		//rollbackFor 안쓰면 런타임예외시에만 롤백함.
@PropertySource("classpath:/config.properties")
public class MyPageServiceImpl implements MyPageService{
	
	private final MyPageMapper mapper;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Value("${my.profile.web-path}")
	private String profileWebPath; //        /myPage/profile/
	
	@Value("${my.profile.folder-path}")
	private String profileFolderPath; //        C:/uploadFiles/profile/
	
	

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

	/**
	 * 파일 업로드 테스트1
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@Override
	public String fileUpload1(MultipartFile uploadFile) throws IllegalStateException, IOException {
		
		// MultipartFile 이 제공하는 메서드
		// - getSize() : 파일 크기
		// - isEmpty() : 업로드한 파일이 '없을 경우' true 를 반환하는 메서드
		// - getOriginalFileName() : 원본 파일 명 ex)pikachu-02.jpg
		// - transferTo(경로) : 
		//			메모리 또는 임시 저장 경로에 업로드된 파일을 
		// 		원하는 경로에 전송하는 일을 합니다. (서버 어떤 폴더에 저장할지 지정)
		//			보통은 어떤 경우 transferTo 를 쓰느냐? 
		//			실제로 사용자가 올린 이미지에 대한 정보가 DB 에 업로드 되었을 때, 
		//			transferTo 를 이용해서 임시 저장 경로가 아닌 실제 서버에 저장할 곳에 해당 이미지를 저장.

		
		
		if(uploadFile.isEmpty()) { // 사용자가 업로드한 파일이 없을 경우
			return null;
		}
		
		log.debug("uploadFile.getOriginalFilename()=={}", uploadFile.getOriginalFilename()); // pikachu-02.jpg
		
		// 업로드한 파일이 있을 경우
		// C:/uploadFiles/test/파일명 으로 서버(현재 애플리케이션이 띄워져 있는 컴퓨터)에 저장할 것
		uploadFile.transferTo(
				new File("C:\\uploadFiles\\test\\" + uploadFile.getOriginalFilename())
				);
		// 웹에서 해당 파일에 접근할 수 있는 경로를 반환.
		
		// 서버 : C:\\uploadFiles\\test\\pikachu-02.jpg
		// 웹 접근 주소 : /myPage/file/pikachu-02.jpg
		
		return "/myPage/file/" + uploadFile.getOriginalFilename();
	}

	// 파일 업로드 테스트2(+DB)
	@Override
	public int fileUpload2(MultipartFile uploadFile, int memberNo) throws IOException {
		
		// 업로드된 파일이 없다면
		// == 사용자가 선택해서 보내준 파일이 없을 경우
		if(uploadFile.isEmpty()) {
			return 0;
		}
		
		/* 근데, 왜 이미지파일을 DB 에 저장안하고, 서버컴퓨터에 저장을 할까?
		 *  DB 부하를 줄이기 위함이다. 파일이 엄청 클 수도 있거든. 
		 *  
		 *  1) DB에는 서버컴퓨터에 이미지를 저장할 파일 경로를 저장함. 
		 *  
		 *  2) DB 삽입/수정 성공 후 서버에 파일을 저장
		 * 
		 *  3) 만약 DB에 파일 저장하다가 실패 시 
		 *  	-> 예외 발생
		 *  	-> @Transactional 을 이용해서 rollback 수행
		 * 
		 * 	그럼, 가장 먼저해야할건? DB 에 저장할 파일경로를 만드는 것
		 * 
		 * */
		
		//  1. 서버에 저장할 파일 경로 만들기
		
		// 파일이 저장될 서버 폴더 경로
		String folderPath = "C:\\uploadFiles\\test\\";
		
		// 클라이언트가 파일이 저장된 폴더에 접근할 수 있는 주소
		String webPath = "/myPage/file/";
		
		// 2. DB에 전달할 데이터를 DTO로 묶어서 INSERT 호출하기 
		// webPath, memberNo, 원본파일명, 변경된 파일명
		String fileRename = Utility.fileRename(uploadFile.getOriginalFilename());
		
		// @Builder 패턴을 사용해서 UploadFile 객체 생성
		// 장점 1) 반복되는 참조변수명, set 구문 생략
		// 장점 2) method chaning 을 이용해서 한 줄로 작성 가능
		UploadFile uf = UploadFile.builder()
				.memberNo(memberNo)
				.filePath(webPath)
				.fileOriginalName(uploadFile.getOriginalFilename())
				.fileRename(fileRename)
				.build();

		// DB 저장
		int result = mapper.insertUploadFile(uf);
		
		// 3. 삽입(INSERT) 성공 시 파일을 지정된 서버 폴더에 저장
		
		// 삽입 실패 시
		if(result == 0) {
			return 0;
		}
		
		// 삽입 성공 시 
		
		// C:\\uploadFiles\\test\\변경된파일명 으로
		// 파일을 서버에 저장
		
		uploadFile.transferTo(
				new File(folderPath + fileRename)
		);
		// IOException 이 발생가능해서 잡아줘야 하는데, IOException 은 체크예외이기 때문에, 
		// @Transactional에 (rollbackFor = Exception.class) 라고 붙여줘야 하는 거 주의

		return result;
	}

	
	// 파일 목록 조회
	@Override
	public List<UploadFile> fileList() {
		
		return mapper.fileList();
	}

	// 여러 파일 업로드
	@Override
	public int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo) throws IOException{
		
		// 1. aaaList 처리
		int result1 = 0;
		
		//업로드된 파일이 없을 경우를 제외하고 업로드
		for(MultipartFile file : aaaList) {
			if(file.isEmpty()) { //파일이 없으면 다음 파일로 넘어갈것이다.
				continue;
			}
			
			// fileUpload2() 메서드 호출(재활용)
			// -> 파일 하나 업로드 + DB INSERT 
			result1 += fileUpload2(file, memberNo);
			
			
			
		}
		
		// 2. bbbList 처리
		int result2 = 0;
		
		//업로드된 파일이 없을 경우를 제외하고 업로드
		for(MultipartFile file : bbbList) {
			if(file.isEmpty()) { //파일이 없으면 다음 파일로 넘어갈것이다.
				continue;
			}
			
			// fileUpload2() 메서드 호출(재활용)
			// -> 파일 하나 업로드 + DB INSERT 
			result2 += fileUpload2(file, memberNo);
		}
		
		
		
		return result1 + result2;
	}

	   // 프로필 이미지 변경
	   @Override
	   public int profile(MultipartFile profileImg, Member loginMember) throws Exception {

	      // 수정할 경로
	      String updatePath = null;
	      
	      // 변경명 저장
	      String rename = null;
	      
	      // 업로드한 이미지가 있을 경우
	      // - 있을 경우 : 수정할 경로 조합 (클라이언트 접근 경로+리네임파일명)
	      // 업로드한 이미지가 없다면 왜 이 if문을 지나가도록 하지 않았을까? 
	      // 이 if 문의 역할이 뭔데?
	      // 파일에 부여할 고유키를 부여하는 역할이 있고, 
	      // 
	      if(!profileImg.isEmpty()) {
	         // updatePath 조합
	         
	         // 1. 파일명 변경
	    	// 확장자를 얻기 위해, 사용자가 올린 이미지의 이름을 파라미터로 전달.
	       // 근데, 왜 이름을 바꿔줄까? 그냥, 사용자가 첨부한 이미지의 이름 그대로를 넣으면 안되?
	    	  // 사용자가 첨부한 이미지의 이름이 엄청 길 수 있잖아? 
	    	  // 그러니까, 일종의 그 이름 대신 그 이미지에 대한 식별키를 부여하는 거지.
	    	  // 이미지에 PK 를 부여했다 생각하면 됨. 
	         rename = Utility.fileRename(profileImg.getOriginalFilename()); 
	         
	         // 2. /myPage/profile/변경된파일명
	         // 이건 왜 해줌? updatePath 라는 변수에 담긴 건, 어떤 역할인데? 
	         // ㄱ. 데이터베이스에 MEMBER 테이블에 PROFILE_IMG 컬럼에 담길 값
	         // ㄴ. ㄱ 과 연관된 말인데, 그렇게 MEMBER 테이블의 PROFILE_IMG 컬럼 값으로 
	         // 		넣어두면 로그인한 MEMBER의 프로필 탭을 딱 누르는 순간
	         // 		MEMBER 테이블에서 행을 꺼내와서 그 PROFILE_IMG 컬럼값을 가지고 
	         //		타임리프로 <img> 태그에 th:src 속성값으로 그 PROFILE_IMG 컬럼값을 박아버리거든?
	         // 		그럼, 프로필 관련 페이지가 로딩되면 브라우저는 곧바로 th:src 속성값으로 HTTP요청을 보내게 된다. 
	         //		근데, 그러려면
	         updatePath = profileWebPath + rename; // 현재 profileWebPath 에는 /myPage/profile/ 이런 데이터가 들어있음. 
	         // 따라서, 현재 updatePath 에는 뭐가 담기게 되냐면, " /myPage/profile/ + 이미지에 부여한 고유키  " 가 담기게 된다. 
	      }
	      
	      // 수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체
	      Member mem = Member.builder()
	            .memberNo(loginMember.getMemberNo())
	            .profileImg(updatePath) // MEMBER 테이블의 PROFILE_IMG 라는 컬럼에 저장될 값으로 뭘 설정하고 있니? 
	            											// 바로 /myPage/profile/이미지에 부여한 고유키 가 저장되어 있음. 
	            .build();
	      
	      // UPDATE 수행
	      int result = mapper.profile(mem); 
	      
	      if(result > 0) {
	         // DB에 수정 성공 시
	         // 프로필 이미지를 없앤 경우(NULL로 수정한 경우)를 제외
	         // -> 업로드한 이미지가 있을 경우
	         if(!profileImg.isEmpty()) {
	            // 파일을 서버 지정된 폴더에 저장
	            profileImg.transferTo(new File(profileFolderPath + rename));
	            // C:/uploadFiles/profile/        +               20240417102705_00004.jpg
	         }
	         
	         // 세션 회원 정보에서 프로필 이미지 경로를
	         // 업데이트한 경로로 변경
	         loginMember.setProfileImg(updatePath);
	      }
	      return result;
	   }
	

}
