package kr.or.oti.youthportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.or.oti.youthportal.dto.UserJoinDTO;
import kr.or.oti.youthportal.service.UserService;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/user")

public class UserController {

	private final UserService userService;

	//로그인 화면 이동
	@GetMapping("/login")
	public String loginGET(HttpSession session) { // 세션을 받아 이미 로그인된 상태인지 확인

		if (session.getAttribute("loginUser") != null) { // 이미 로그인한 상태라면
			return "redirect:/"; // 로그인 화면으로 다시 갈 필요 없이 메인으로 보냄
		}

		return "user/login"; // 로그인 화면 뷰 이름 반환
	}

	// 로그인 처리(POST /user/login)와 로그아웃(/user/logout)은 SecurityConfig의 formLogin/logout이 직접 가로채서 처리함

	//회원가입 화면 이동
	@GetMapping("/join")
	public void joinGET() {

	}

	//회원가입 처리
	@PostMapping("/join")
	public String joinPOST(UserJoinDTO userJoinDTO , RedirectAttributes rttr) {
		try {
			userService.join(userJoinDTO);
		} catch (IllegalStateException e) {
			rttr.addFlashAttribute("error" , e.getMessage());
			return "redirect:/user/join";
		}

		rttr.addFlashAttribute("result" , "회원가입 성공!");
		return "redirect:/user/login";
	}
}
