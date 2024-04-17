package edu.kh.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
@PropertySource("classpath:/config.properties")
public class FileConfig implements WebMvcConfigurer{
	
	// WebMvcConfigurer : Spring MVC 프레임워크에서 제공하는 인터페이스 중 하나로,
	// 스프링 구성을 커스터마이징하고 확장하기 위한 메서드를 제공함. 
	// 주로 웹 애플리케이션의 설정을 조정하거나 추가하는데 사용됨. 
	
	
	// 파일 업로드 임계값
	@Value("${spring.servlet.multipart.file-size-threshold}")
	private long fileSizeThreshold;
	
	// 요청당 파일 최대 크기
	@Value("${spring.servlet.multipart.max-request-size}")
	private long maxRequestSize;
	
	// 개별 파일당 최대 크기
	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize;
	
	// 파일 업로드 임계값 초과시 임시 저장 폴더 경로를 가리킴
	@Value("${spring.servlet.multipart.location}")
	private String location;
	
	//==================================================
	// 프로필 이미지
	@Value("${my.profile.resource-handler}")
	private String profileResourceHandler;
	
	// 프로필 이미지
	@Value("${my.profile.resource-location}")
	private String profileResourceLocation;
	
	//===================================================

	
	// 요청 주소(a 태그의 href 나 img 태그의 src속성을 말함)에 따라
	// 서버 컴퓨터의 어떤 경로에 접근할지 설정해줄 수 있다. 
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/myPage/file/**") //클라이언트의 요청 주소 패턴
		.addResourceLocations("file:///C:/uploadFiles/test/");
		// 클라이언트가 /myPage/file/** 패턴으로 이미지를 요청할 때, 
		// 요청을 연결해서 처리해줄 서버 폴더 경로를 연결한 것.
		
		// 프로필 이미지 요청 - 서버 폴더 연결 추가
		registry.addResourceHandler(profileResourceHandler)
		.addResourceLocations(profileResourceLocation); // 이렇게 registry 를 추가해주면 됨. 
	
		/*
		 *                     file:///C: 는 파일 시스템의 루트 디렉토리(최상위경로)
		 *                     file://   은 URL 스킴(Scheme), 파일 시스템의 리소스
		 *                     /C:     는 Windows 시스템에서 C드라이브를 가리킴
		 *                     file:///C:     는 "C 드라이브의 루트 디렉토리" 를 의미한다고 보면 됨.  좀 더 쉽게 표현하면, "C 드라이브" 를 의미한다고 보면 됨. 
		 *                     
		 * */
	}

	
	/* MultipartResolver 설정 */
	@Bean
	public MultipartConfigElement configElement() {
		// MultipartConfigElement : 
		// 파일 업로드를 처리하는데 사용되는 MultipartConfigElement 를 구성하고 반환
		// 파일 업로드를 위한 구성 옵션을 설정하는데 사용
		// 업로드 파일의 최대크키를 몇까지 허용할것인가?
		// 메모리에서의 임시 저장 경로 등을 설정할 수 있다. 
		
		// -> 서버 경로(서버컴퓨터의 진짜경로) 를 작성하기 때문에, 보안상 문제가 생길 수 있기 때문에,
		// config.properties 에 작성하도록 함.
		
		// MultipartConfigFactory 는 multipart 에 대한 설정을 해주는 공장이라고 생각하면 됨. 
		MultipartConfigFactory factory = new MultipartConfigFactory();
		
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));
		
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
		
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));
		
		factory.setLocation(location);
		return factory.createMultipartConfig();
		
	}
	
	@Bean // MultipartResolver 객체를 Bean 으로 추가 -> 스프링이 빈으로 추가 후 위에서 만든 MultipartConfigElement 를 자동으로 이용할거임.
	public MultipartResolver multipartResolver () {
		// MultipartResolver : MultipartFile 을 처리해주는 해결사라고 생각하면됨. 
		// MultipartResolver는 클라이언트로부터 받은 멀티파트 요청을 처리하고,
		// 이 중에서 업로드된 파일을 추출하여 MultipartFile 객체로 제공하는 역할
		StandardServletMultipartResolver multipartResolver
			= new StandardServletMultipartResolver();
		return multipartResolver;
	}
	
	
	
	
	
}
