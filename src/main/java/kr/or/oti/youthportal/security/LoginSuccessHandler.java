package kr.or.oti.youthportal.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.oti.youthportal.dto.UserDTO;
import kr.or.oti.youthportal.mapper.UserDAO;
import lombok.RequiredArgsConstructor;

// 폼 로그인/카카오 로그인 공용 - 로그인 성공 시 누적된 실패횟수를 초기화하는 역할
// 로그인 사용자 정보는 SecurityContext의 principal(CustomUserDetails)이 유일한 출처이며, 별도로 세션에 복사하지 않음
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserDAO userDAO;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		UserDTO user = principal.getUser();

		if (user.getFailCount() > 0) { // 과거 로그인 실패 기록이 남아있다면 초기화
			UserDTO resetTarget = UserDTO.builder()
					.userId(user.getUserId())
					.del(user.isDel())
					.failCount(0)
					.accountLocked(false)
					.build();
			userDAO.updateMemberByAdmin(resetTarget);
		}

		response.sendRedirect(request.getContextPath() + "/");
	}
}
