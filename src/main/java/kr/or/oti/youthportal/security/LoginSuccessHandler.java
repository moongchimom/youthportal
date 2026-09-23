package kr.or.oti.youthportal.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.oti.youthportal.domain.User;
import kr.or.oti.youthportal.dto.UserDTO;
import kr.or.oti.youthportal.mapper.UserDAO;
import lombok.RequiredArgsConstructor;

// 폼 로그인/카카오 로그인 공용 - 로그인 성공 시 header.html 등 기존 화면이 그대로 읽는 세션 속성을 채워주는 역할
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserDAO userDAO;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		UserDTO user = principal.getUser();

		if (user.getFailCount() > 0) { // 과거 로그인 실패 기록이 남아있다면 초기화 (기존 UserServiceImpl.login()과 동일한 로직)
			User resetTarget = User.builder()
					.userId(user.getUserId())
					.del(user.isDel())
					.failCount(0)
					.accountLocked(false)
					.build();
			userDAO.updateMemberByAdmin(resetTarget);
		}

		HttpSession session = request.getSession();
		session.setAttribute("loginUser", user.getUserId());
		session.setAttribute("loginUserName", user.getName());
		session.setAttribute("loginRole", user.isRole());

		response.sendRedirect(request.getContextPath() + "/");
	}
}
