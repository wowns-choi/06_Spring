package edu.kh.project.common.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:/config.properties")
public class EmailConfig {
		
	// @Value : properties 에 작성된 내용 중 키가 일치하는 값을 얻어와 필드에 대입.
	
	@Value("${spring.mail.username}")
	private String userName;
	
	@Value("${spring.mail.password}")
	private String password;
	
	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties prop = new Properties(); // Map 후손이어서, key:value 형식으로 데이터저장하는 자료구조
		prop.setProperty("mail.transport.protocol", "smtp"); // 전송 프로토콜로 smtp 를 이용하겠다.
		prop.setProperty("mail.smtp.auth", "true"); //인증을 사용하겠다?
		prop.setProperty("mail.smtp.starttls.enable", "true"); // 안전한 연결 활성화 할지의 여부.
		prop.setProperty("mail.debug", "true"); //디버그 모드 사용할지 여부
		prop.setProperty("mail.smtp.ssl.trust","smtp.gmail.com"); 
		prop.setProperty("mail.smtp.ssl.protocols","TLSv1.2"); //
		
		
		mailSender.setUsername(userName);
		mailSender.setPassword(password);
		mailSender.setHost("smtp.gmail.com"); // smtp 서버 호스트 설정
		mailSender.setPort(587); // smtp 서버 포트 설정
		mailSender.setDefaultEncoding("UTF-8");
		mailSender.setJavaMailProperties(prop);
		return mailSender;
	}
	
	
}
