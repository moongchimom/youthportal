package kr.or.oti.youthportal.service; // 서비스(비즈니스 로직) 패키지

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import kr.or.oti.youthportal.dto.UserDTO;
import kr.or.oti.youthportal.dto.UserJoinDTO;
import kr.or.oti.youthportal.mapper.UserDAO;
import lombok.RequiredArgsConstructor;

@Service // 스프링이 이 클래스를 서비스 빈으로 등록
@RequiredArgsConstructor // userDAO를 주입받는 생성자를 자동 생성

public class UserServiceImpl implements UserService{

	private final UserDAO userDAO; // 회원 관련 SQL을 수행하는 매퍼
	private final PasswordEncoder passwordEncoder; // 회원가입 시 비밀번호 해시용 (로그인 검증은 Spring Security가 담당)

	// 로그인 처리는 CustomUserDetailsService + DaoAuthenticationProvider(+LoginSuccessHandler/LoginFailureHandler)로 이동함

	@Override
	public void join(UserJoinDTO userJoinDTO) {
		//중복확인
		int count = userDAO.checkUserId(userJoinDTO.getUserId()); // 아이디 중복 여부 조회
		if(count > 0) { // 이미 존재하는 아이디라면
			throw new IllegalStateException("이미 존재하는 아이디입니다."); // 가입 중단
		}

		if (!userJoinDTO.getUpw().equals(userJoinDTO.getUpwConfirm())) { // 비밀번호와 비밀번호 확인이 다르면
			throw new IllegalStateException("비밀번호가 일치하지 않습니다."); // 가입 중단
		}

		//UserJoinDTO -> 저장용 UserDTO 변환

		UserDTO user = UserDTO.builder()
		        .userId(userJoinDTO.getUserId())
		        .upw(passwordEncoder.encode(userJoinDTO.getUpw())) // BCrypt로 해시해서 저장 (평문 저장 금지)
		        .name(userJoinDTO.getName())
		        .birthDate(userJoinDTO.getBirthDate())
		        .phone(userJoinDTO.getPhone())
		        .regDate(LocalDateTime.now())
		        .role(false)              // 일반 회원
		        .del(false)               // 정상 (미탈퇴)
		        .failCount(0)            // 실패 횟수 0
		        .accountLocked(false)     // 잠김 해제 상태
		        .build();

		userDAO.insertUser(user);
	}


}
