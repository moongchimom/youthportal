package kr.or.oti.youthportal.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import kr.or.oti.youthportal.dto.UserDTO;
import lombok.Getter;

// 폼 로그인(UserDetails)과 카카오 로그인(OAuth2User)이 하나의 클래스를 같이 쓰도록 통일
// - LoginSuccessHandler가 로그인 수단과 무관하게 같은 코드로 세션을 채울 수 있게 하기 위함
@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

	private final UserDTO user; // 실제 회원 정보 (USERS 테이블 한 행)
	private final Map<String, Object> attributes; // 카카오가 준 원본 프로필 (폼 로그인이면 빈 맵)

	public CustomUserDetails(UserDTO user) { // 폼 로그인용 생성자
		this(user, Collections.emptyMap());
	}

	public CustomUserDetails(UserDTO user, Map<String, Object> attributes) { // 카카오 로그인용 생성자
		this.user = user;
		this.attributes = attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.isRole() // 관리자는 ROLE_ADMIN도 함께 부여 (일반 권한도 유지)
				? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
				: List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return user.getUpw();
	}

	@Override
	public String getUsername() {
		return user.getUserId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 계정 만료 개념은 이 프로젝트에 없음
	}

	@Override
	public boolean isAccountNonLocked() {
		return !user.isAccountLocked(); // 로그인 실패 누적으로 잠긴 계정이면 false
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 비밀번호 만료 개념은 이 프로젝트에 없음
	}

	@Override
	public boolean isEnabled() {
		return !user.isDel(); // 탈퇴 처리된 계정이면 false
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return user.getUserId();
	}
}
