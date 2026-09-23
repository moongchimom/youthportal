package kr.or.oti.youthportal.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.or.oti.youthportal.dto.UserDTO;
import kr.or.oti.youthportal.mapper.UserDAO;
import lombok.RequiredArgsConstructor;

// 폼 로그인 시 DaoAuthenticationProvider가 아이디로 회원을 조회할 때 사용 (비밀번호 비교는 프레임워크가 알아서 처리)
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserDAO userDAO;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		UserDTO user = userDAO.selectById(userId); // 기존 로그인 로직과 동일한 조회 메서드 재사용

		if (user == null) { // 존재하지 않는 아이디
			throw new UsernameNotFoundException("존재하지 않는 아이디입니다: " + userId);
		}

		return new CustomUserDetails(user);
	}
}
