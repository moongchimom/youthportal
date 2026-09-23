package kr.or.oti.youthportal.controller; // 컨트롤러(웹 요청 처리) 패키지

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 4개의 주요 컨트롤러(User/Policy/Admin/Application)는 모두 클래스 레벨 @RequestMapping 프리픽스가 있어
// 루트 경로("/")를 처리할 수 없으므로, 메인 화면 진입만을 위한 최소한의 컨트롤러를 별도로 둔다.
@Controller // 스프링이 이 클래스를 웹 컨트롤러 빈으로 등록
public class HomeController {

	// 메인(홈) 화면 - 별도의 DB 조회 없이 정적인 소개/이동 화면만 보여줌
	@GetMapping("/")
	public String home() {
		return "index"; // templates/index.html 반환
	}
}
