package edu.kh.project.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/*@Configuration
 * - 해당 클래스가 설정용 클래스임을 명시하는 역할을 하는 애노테이션임.
 * 	   + 스프링 컨테이너가 객체(빈, 싱글톤)로 생성해서 내부 코드를 서버 실행시 모두 수행
 * 	  
 * @PropertySource("경로")
 * - 지정된 경로의 properties 파일 내용을 읽어와 사용
 * - 사용할 properties 파일이 다수일 경우
 * 	   해당 어노테이션을 연속해서 작성하면 됨.
 * 
 * @ConfigurationProperties(prefix="여기")
 * - @PropertySource 로 읽어온 properties 파일의 내용 중
 * 		접두사가 일치하는 값만 읽어옴.
 * 		
 * 
 * @Bean 
 * - 수동빈생성할때 쓰잖아
 * 
 * DataSource : Connection 생성 + Connection Pool 지원하는 객체를
 * 							참조하기 위한 Java 인터페이스
 * 							(DriverManager 대안, Java JNDI 기술이 적용되어 있는 객체이다)
 * 
 * @Autowired
 * - 등록된 Bean 중에서
 * 		타입이 일치하거나, 상속관계에 있는 Bean을 
 * 		지정된 필드에 주입
 * 		==의존성 주입(DI, Dependency Injection)
 * 
 * */

@Configuration
@PropertySource("classpath:/config.properties")
public class DBConfig {
	
	@Autowired
	private ApplicationContext applicationContext; //application scope 객체만들때 사용된 클래스가 ApplicationContext 이다.
					// 즉 ApplicationContext 타입 객체는 현재프로젝트를 가리킨다. 
	
	
	
	///////////////////// HikariCP 설정 시작 ////////////////
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.hikari")
	public HikariConfig hikariConfig () {
		return new HikariConfig();
	}
	
	@Bean
	public DataSource dataSource(HikariConfig config) {
		// 매개변수 HikariConfig config
		// -> 등록된 Bean 중 HikariConfig 타입의 Bean 자동으로 주입
		DataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}
	//////////////////////HikariCP 설정 끝 ///////////////////////////////
	
	
	////////////////////// Mybatis 설정 시작 //////////////////////////////
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource datasource) throws Exception {
		
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(datasource);
		
		//mapper.xml(SQL) 파일이 모이는 경로를 지정해줘야함. 
		// -> Mybatis 코드 수행시 mapper.xml 을 읽을 수 있음. 
		// sessionFactoryBean.setMapperLocations("현재프로젝트.자원.어떤파일");
		
		sessionFactoryBean.setMapperLocations(
				applicationContext.getResources("classpath:/mappers/**.xml") 
				);
		
		// 해당 패키지 내 모든 클래스의 별칭을 등록
		// -Mybatis 는 특정 클래스 지정 시 패키지명.클래스명을 모두 작성해야함
		// -> 너무 길다. 긴 이름을 짧게 부를 별칭 설정할 수 있음.
		
		// setTypeAliasesPackage("패키지") 이용 시
		// 클래스 파일명이 별칭으로 등록된다. 
		
		// ex) edu.kh.todo.model.dto.Todo --> Todo(별칭 등록)
		
		sessionFactoryBean.setTypeAliasesPackage("edu.kh.project"); // 이건 뭐냐면, 해당 패키지 내에 있는 모든 클래스에 별칭을 등록하는 것.
		//xml 파일에서 namespace 부분라든지, resultType 같은 곳에 패키지 안쓰려고 한듯.
		
		// 마이바티스 설정 파일 경로 지정
		sessionFactoryBean.setConfigLocation(
					applicationContext.getResource("classpath:/mybatis-config.xml")
				);
		
		return sessionFactoryBean.getObject();
	}
	
	////////////////////// Mybatis 설정 끝 //////////////////////////////
	
	// SqlSessionTemplate : Connection 생성 + DBCP(DBconnectionPool) + Mybatis + 트랜잭션 제어 처리
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sessionFactory) {
	return new SqlSessionTemplate(sessionFactory);
	}

	// DataSourceTransactionManager : 트랜잭션 매니저
	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
	return new DataSourceTransactionManager(dataSource);
	}
	

}