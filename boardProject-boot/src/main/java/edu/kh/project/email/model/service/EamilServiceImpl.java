package edu.kh.project.email.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.email.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional //예외 발생하면 롤백할게~ (기본값 커밋)
@Slf4j
@RequiredArgsConstructor // final 필드 / @NotNull 필드에 자동으로 의존성 주입 (@Autowired  생성자 방식 코드 자동완성)
public class EamilServiceImpl implements EmailService{

	//Mapper 의존성 주입
	private final EmailMapper mapper;
	
	//EmailConfig 설정이 적용된 객체(메일 보내기 기능)
	private final JavaMailSender mailSender;

	// 이메일 보내기 
	@Override
	public String sendEamil(String htmlName, String email) {
		// htmlName 은 "signup" 이 들어와 있음. 
		// email 은 사용자가 입력한 이메일로서, 지금 이메일 보낼 이메일주소. 
		
		//난수 6자리 생성메서드가 createAuthKey 임. 
		String authKey = createAuthKey(); // 이메일 인증의 내용으로 보낼 난수 6자리.
		
		try {
			
			// 보낼 메일의 제목 정하기
			String subject = null;
			
			switch(htmlName) {
			case "signup" : 
				subject = "[boardProject] 회원 가입 인증번호 입니다."; 
				break;
			}
			
			// 인증 메일 보내기 
			
			//MimeMessage(jakarta.mail.internet.MimeMessage) : Java에서 메일을 보내는 객체이다. 
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			//MimeMessageHelper :
			// Spring 에서 제공하는 메일 발송 도우미 <- 이걸 이용하면, 좀 더 간단히 메일을 보낼 수 있고, 타임리프도 이용가능
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			
			// 1번 매개변수 : MimeMessage
			// 2번 매개변수 : 파일 전송 사용 ? true / false
			// 3번 매개변수 : 문자 인코딩 지정
			
			helper.setTo(email); // 받는 사람 이메일 지정
			helper.setSubject(subject); // 이메일 제목 지정
			
			helper.setText(authKey); // 조만간 변경해볼거임. html 보내도록 해줄거임. 
			
			// CID (Content - ID) 를 이용해 메일에 이미지 첨부할 수 있음. 
			// 나중에 로고 추가해볼것.
			
			// 메일 보내기
			mailSender.send(mimeMessage);
			
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
		return null;
	}
	
	/** 인증번호 생성 (영어 대문자 + 소문자 + 숫자 6자리)
	    * @return authKey
	    */
	   public String createAuthKey() {
	   	String key = "";
	       for(int i=0 ; i< 6 ; i++) {
	          
	           int sel1 = (int)(Math.random() * 3); // 0:숫자 / 1,2:영어
	          
	           if(sel1 == 0) {
	              
	               int num = (int)(Math.random() * 10); // 0~9
	               key += num;
	              
	           }else {
	              
	               char ch = (char)(Math.random() * 26 + 65); // A~Z
	              
	               int sel2 = (int)(Math.random() * 2); // 0:소문자 / 1:대문자
	              
	               if(sel2 == 0) {
	                   ch = (char)(ch + ('a' - 'A')); // 대문자로 변경
	               }
	              
	               key += ch;
	           }
	          
	       }
	       return key;
	   }

	
	

}
/*Google SMTP 를 이용한 이메일 전송하기
 * 
 * - SMTP (Simple Mail Transfer Protocol, 간단한 메일 전송 규약)
 * --> 이메일 메세지를 보내고 받을 때 사용하는 프로토콜(규약, 규칙약속)
 * 
 * -- Google SMTP, 야후도 제공한다고 함..
 * 
 * 		Java Mail Sender -build.gradle : implementation 'org.springframework.boot:spring-boot-starter-mail'
 * 		를 이용해서, Google SMTP 를 활용할 것. 그러면, 대상에게 이메일이 전송되는 흐름일 것이다. 
 * 		
 * 	-	Java Mail Sender 에 Google SMTP 이용 설정 추가
 * 1) config.properties 내용 추가(계정, 앱비밀번호) : 이걸 설정해줘야 Java Mail Sender 를 이용해서 Google SMTP 를 사용할 수 있음.
 * 2) EmailConfig.java 라는 클래스를 하나 만들어줄거임 : Java Mail Sender 에 대한 설정을 하는 클래스
 * 
 * */


