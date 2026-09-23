package kr.or.oti.youthportal.security;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import kr.or.oti.youthportal.domain.User;
import kr.or.oti.youthportal.dto.UserDTO;
import kr.or.oti.youthportal.mapper.UserDAO;
import lombok.RequiredArgsConstructor;

// 카카오 로그인 콜백에서 받은 프로필을 우리 회원(USERS)으로 매핑/등록
// 실제 인가코드->토큰 교환, 토큰으로 프로필 조회하는 HTTP 통신 자체는 Spring Security(DefaultOAuth2UserService)가 알아서 처리해줌
@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserDAO userDAO;
	private final PasswordEncoder passwordEncoder;

	private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService(); // 실제 카카오 API 호출은 기본 구현에 위임

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User kakaoUser = delegate.loadUser(userRequest); // 카카오 프로필 원본 (id, kakao_account 등)

		Long kakaoId = (Long) kakaoUser.getAttributes().get("id"); // 카카오 고유 회원번호
		String nickname = extractNickname(kakaoUser.getAttributes()); // 닉네임 (동의 안 했으면 기본값으로 대체)

		String userId = "kakao_" + kakaoId; // 내부 아이디로 합성 (USERS.USER_ID 기본키에 맞춤)

		UserDTO existing = userDAO.selectById(userId); // 이미 연동된 계정인지 확인

		if (existing == null) { // 처음 카카오로 로그인하는 사용자라면 신규 등록
			User newUser = User.builder()
					.userId(userId)
					.upw(passwordEncoder.encode(UUID.randomUUID().toString())) // 카카오 로그인은 비밀번호를 안 쓰지만 컬럼이 NOT NULL이라 랜덤값으로 채움
					.name(nickname)
					.birthDate(null) // 카카오 기본 동의항목에는 생년월일이 없음
					.phone(null)
					.regDate(LocalDateTime.now())
					.role(false)
					.del(false)
					.failCount(0)
					.accountLocked(false)
					.socialType("KAKAO")
					.socialId(String.valueOf(kakaoId))
					.build();

			userDAO.insertUser(newUser);
			existing = userDAO.selectById(userId); // 방금 등록한 행을 다시 조회해서 DB 기본값까지 반영된 최신 상태로 사용
		}

		if (existing.isDel()) { // 관리자가 탈퇴 처리한 계정이면 카카오로도 로그인 못 하게 막음
			throw new OAuth2AuthenticationException("탈퇴한 계정입니다. 로그인할 수 없습니다.");
		}

		if (existing.isAccountLocked()) { // 잠긴 계정도 동일
			throw new OAuth2AuthenticationException("로그인 실패 횟수 초과로 잠긴 계정입니다. 관리자에게 문의하세요.");
		}

		return new CustomUserDetails(existing, kakaoUser.getAttributes());
	}

	// kakao_account.profile.nickname을 안전하게 꺼내고, 없으면 기본값으로 대체
	@SuppressWarnings("unchecked")
	private String extractNickname(Map<String, Object> attributes) {
		Object kakaoAccountObj = attributes.get("kakao_account");
		if (kakaoAccountObj instanceof Map) {
			Object profileObj = ((Map<String, Object>) kakaoAccountObj).get("profile");
			if (profileObj instanceof Map) {
				Object nickname = ((Map<String, Object>) profileObj).get("nickname");
				if (nickname != null) {
					return nickname.toString();
				}
			}
		}
		return "카카오사용자";
	}
}
