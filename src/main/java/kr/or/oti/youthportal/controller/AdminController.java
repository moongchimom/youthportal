package kr.or.oti.youthportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.oti.youthportal.dto.AdminMemberModifyDTO;
import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;
import kr.or.oti.youthportal.dto.UserDTO;
import kr.or.oti.youthportal.dto.ApplicationDTO;
import kr.or.oti.youthportal.service.AdminService;
import kr.or.oti.youthportal.service.PolicyService;
import lombok.RequiredArgsConstructor;


@Controller // 스프링이 이 클래스를 웹 컨트롤러 빈으로 등록
@RequiredArgsConstructor // adminService를 주입받는 생성자를 자동 생성
@RequestMapping("/admin") // /admin으로 시작하는 요청을 이 컨트롤러가 처리 (SecurityConfig가 관리자 권한 접근을 제어)

public class AdminController {
	private final AdminService adminService; // 회원/신청 관련 관리자 비즈니스 로직 서비스
	private final PolicyService policyService; // 정책 관련 관리자 비즈니스 로직 서비스

	// 회원 목록 화면
	@GetMapping("/member/list")
	public void memberList(PageRequestDTO pageRequestDTO, Model model) {
		PageResponseDTO<UserDTO> responseDTO = adminService.getMemberList(pageRequestDTO); // 검색조건에 맞는 회원 목록+페이징 조회
		model.addAttribute("responseDTO", responseDTO); // 뷰에 전달
		model.addAttribute("pageRequestDTO", pageRequestDTO); // 검색폼 값 유지용
	}

	// 회원 상세(+수정) 화면
	@GetMapping("/member/read")
	public void memberRead(String userId, Model model) {
		UserDTO member = adminService.getMember(userId); // 회원 상세 조회
		model.addAttribute("member", member); // 뷰에 전달
	}

	// 회원 정보 수정 처리 (탈퇴/잠금/실패횟수 초기화)
	@PostMapping("/member/modify")
	public String memberModify(AdminMemberModifyDTO dto, RedirectAttributes rttr) {
		adminService.modifyMember(dto); // 회원 정보 수정
		//addFlashAttribute는 1회성 메시지 전달, addAttribute는 쿼리스트링으로 전달됨
		rttr.addFlashAttribute("result", "회원 정보가 수정되었습니다."); // 성공 메시지
		rttr.addAttribute("userId", dto.getUserId()); // 리다이렉트 쿼리스트링에 userId 포함
		return "redirect:/admin/member/read"; // 수정한 회원의 상세 화면으로 되돌아감
	}

	// 전체 신청 목록 화면
	@GetMapping("/application/list")
	public void applicationList(PageRequestDTO pageRequestDTO, Model model) {
		PageResponseDTO<ApplicationDTO> responseDTO = adminService.getApplicationList(pageRequestDTO); // 검색조건에 맞는 전체 신청 목록+페이징 조회
		model.addAttribute("responseDTO", responseDTO); // 뷰에 전달
		model.addAttribute("pageRequestDTO", pageRequestDTO); // 검색폼 값 유지용
	}

	// 신청 상태 변경 처리 (승인/반려)
	@PostMapping("/application/status")
	public String applicationStatusModify(Long appNo, String status, RedirectAttributes rttr) {
		try {
			adminService.updateApplicationStatus(appNo, status); // 신청 상태 변경
			rttr.addFlashAttribute("result", "신청 상태가 변경되었습니다."); // 성공 메시지
		} catch (IllegalArgumentException e) { // 허용되지 않은 상태값이 전달된 경우 (정상적인 화면 조작으로는 발생하지 않음)
			rttr.addFlashAttribute("error", e.getMessage()); // 오류 메시지 전달
		}
		return "redirect:/admin/application/list"; // 전체 신청 목록으로 되돌아감
	}

	@PostMapping("/policy/import")
	public String importPolicies(RedirectAttributes rttr){
		int importedCount = policyService.importFromApi(); // 외부 API에서 정책 가져오기
		rttr.addFlashAttribute("result", importedCount + "건의 정책이 가져와졌습니다."); // 성공 메시지 전달
		return "redirect:/admin/application/list"; // 전체 신청 목록으로 되돌아감
	}
}
