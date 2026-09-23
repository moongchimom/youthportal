package kr.or.oti.youthportal.security;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.oti.youthportal.dto.UserDTO;
import kr.or.oti.youthportal.mapper.UserDAO;
import lombok.RequiredArgsConstructor;

// 폼 로그인/카카오 로그인 공용 - 실패 시 기존과 동일한 실패횟수 증가/잠금 로직을 수행하고,
// login.html의 ${error} 플래시 메시지를 그대로 유지시켜줌 (DispatcherServlet을 거치지 않는 시점이라 RedirectAttributes를 못 써서 직접 구현)
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private static final int MAX_FAIL_COUNT = 5; // 로그인 실패 허용 횟수 (이 횟수에 도달하면 계정 잠금)

	private final UserDAO userDAO;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String message = resolveMessage(request, exception); // 실패 사유별 안내 메시지 결정 (+ 비번오류면 실패횟수 증가)

		FlashMap flashMap = new FlashMap(); // RedirectAttributes.addFlashAttribute와 동일한 효과를 필터 단계에서 직접 구현
		flashMap.put("error", message);
		new SessionFlashMapManager().saveOutputFlashMap(flashMap, request, response); // 만료시간 설정 등은 내부에서 알아서 처리해줌

		response.sendRedirect(request.getContextPath() + "/user/login");
	}

	private String resolveMessage(HttpServletRequest request, AuthenticationException exception) {
		if (exception instanceof DisabledException) { // CustomUserDetails.isEnabled() == false (탈퇴 계정)
			return "탈퇴한 계정입니다. 로그인할 수 없습니다.";
		}

		if (exception instanceof LockedException) { // CustomUserDetails.isAccountNonLocked() == false (이미 잠긴 계정)
			return "로그인 실패 횟수 초과로 잠긴 계정입니다. 관리자에게 문의하세요.";
		}

		if (exception instanceof OAuth2AuthenticationException) { // KakaoOAuth2UserService에서 던진 탈퇴/잠김 메시지 그대로 사용
			return exception.getMessage();
		}

		if (exception instanceof BadCredentialsException) { // 아이디 없음 또는 비밀번호 불일치
			return handleBadCredentials(request.getParameter("userId"));
		}

		return "아이디 또는 비밀번호가 일치하지 않습니다";
	}

	// 비밀번호가 틀린 경우에만 실패횟수를 증가시키고, 5회째 도달 시 잠금 처리 (기존 UserServiceImpl.login()과 동일한 로직)
	private String handleBadCredentials(String userId) {
		if (userId == null || userId.isBlank()) {
			return "아이디 또는 비밀번호가 일치하지 않습니다";
		}

		UserDTO user = userDAO.selectById(userId);

		if (user == null) { // 존재하지 않는 아이디는 실패횟수를 늘릴 대상이 없으므로 그대로 안내만
			return "아이디 또는 비밀번호가 일치하지 않습니다";
		}

		int failCount = user.getFailCount() + 1;
		boolean lock = failCount >= MAX_FAIL_COUNT;

		UserDTO updateTarget = UserDTO.builder()
				.userId(user.getUserId())
				.del(user.isDel())
				.failCount(failCount)
				.accountLocked(lock)
				.build();

		userDAO.updateMemberByAdmin(updateTarget);

		if (lock) {
			return "비밀번호 " + MAX_FAIL_COUNT + "회 오류로 계정이 잠겼습니다.";
		}

		return "아이디 또는 비밀번호가 일치하지 않습니다";
	}
}
