package kr.or.oti.youthportal; // 프로젝트 루트 패키지 선언

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 컴포넌트 스캔 + 자동설정 + 설정클래스 지정을 한번에 처리하는 어노테이션
public class YouthportalApplication { // 청년정책포탈 애플리케이션 진입점 클래스

	public static void main(String[] args) { // 자바 애플리케이션의 시작 메서드
		SpringApplication.run(YouthportalApplication.class, args); // 내장 톰캣을 띄우고 스프링 컨테이너를 구동
	}

}
