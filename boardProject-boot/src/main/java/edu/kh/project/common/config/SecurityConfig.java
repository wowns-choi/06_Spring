package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/* @Configuration
 * 		- 설정용 클래스임을 명시해주는 것
 * 		+ 객체로 생성해서 내부 코드를 서버 실행시 모두 수행해준다. 그래서 설정용클래스로 만들어주는 애노테이션이라고 해주는 거야. 
 *		 	 
 *	 @Bean 
		- 개발자가 수동으로 생성한 객체의 관리를
		스프링에게 넘기는 어노테이션 (Bean 등록)
 * */

@Configuration
public class SecurityConfig {
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder () {
		return new BCryptPasswordEncoder();
	}
	
    
}
