package kr.or.oti.youthportal.service; // 서비스 인터페이스 패키지 선언

import kr.or.oti.youthportal.dto.UserJoinDTO;

public interface UserService { // 회원 관련 비즈니스 로직 인터페이스
	// 로그인 처리는 Spring Security(CustomUserDetailsService + DaoAuthenticationProvider)가 담당함

	void join(UserJoinDTO userJoinDTO); // 회원가입 처리
}
