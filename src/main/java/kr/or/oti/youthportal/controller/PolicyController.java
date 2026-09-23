package kr.or.oti.youthportal.controller; // 컨트롤러(웹 요청 처리) 패키지

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;
import kr.or.oti.youthportal.dto.PolicyDTO;
import kr.or.oti.youthportal.service.PolicyService;
import lombok.RequiredArgsConstructor;

@Controller // 스프링이 이 클래스를 웹 컨트롤러 빈으로 등록
@RequiredArgsConstructor // policyService를 주입받는 생성자를 자동 생성
@RequestMapping("/policy") // /policy로 시작하는 요청을 이 컨트롤러가 처리

public class PolicyController {

	private final PolicyService policyService; // 정책 관련 비즈니스 로직 서비스

	// 정책 목록 화면 (검색+페이징)
	@GetMapping("/list")
	public void list(PageRequestDTO pageRequestDTO, Model model) {
		PageResponseDTO<PolicyDTO> responseDTO = policyService.getList(pageRequestDTO); // 검색조건에 맞는 목록+페이징 정보 조회
		model.addAttribute("responseDTO", responseDTO); // 뷰에서 목록/페이징을 그리기 위해 전달
		model.addAttribute("pageRequestDTO", pageRequestDTO); // 검색폼 값 유지 및 페이지네이션 링크 생성을 위해 전달
		model.addAttribute("regionList", policyService.getRegionList()); // 지역구 라디오 버튼 목록
	}

	// 정책 상세 화면
	@GetMapping("/read")
	public void read(Long pno, PageRequestDTO pageRequestDTO, Model model) {
		PolicyDTO policyDTO = policyService.getPolicy(pno); // 정책 상세 조회
		model.addAttribute("dto", policyDTO); // 상세 데이터 전달
		model.addAttribute("pageRequestDTO", pageRequestDTO); // "목록으로" 링크에서 검색조건을 유지하기 위해 전달
	}
}
