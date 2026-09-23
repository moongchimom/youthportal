package kr.or.oti.youthportal.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import kr.or.oti.youthportal.dto.ApplicationApplyDTO;
import kr.or.oti.youthportal.dto.ApplicationDTO;
import kr.or.oti.youthportal.dto.ApplicationFileDTO;
import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;
import kr.or.oti.youthportal.dto.PolicyDTO;
import kr.or.oti.youthportal.dto.UploadFileDTO;
import kr.or.oti.youthportal.dto.UploadResultDTO;
import kr.or.oti.youthportal.service.ApplicationService;
import kr.or.oti.youthportal.service.PolicyService;
import lombok.RequiredArgsConstructor;

@Controller // 스프링이 이 클래스를 웹 컨트롤러 빈으로 등록
@RequestMapping("/application") // /application으로 시작하는 요청을 이 컨트롤러가 처리
@RequiredArgsConstructor // applicationService, policyService를 주입받는 생성자를 자동 생성

public class ApplicationController {

	private final ApplicationService applicationService; // 신청 관련 비즈니스 로직 서비스
	private final PolicyService policyService; // 신청 대상 정책 조회를 위한 서비스
	private final UpDownController upDownController; // 파일 업로드 저장을 실제로 위임할 공용 업로드 컨트롤러 (같은 패키지라 import 불필요)

	@Value("${app.upload-dir}") // application.properties에 정의한 업로드 저장 경로 주입
	private String uploadDir;

	// 신청서 작성 화면 (신청 대상 정책 요약 표시)
	@GetMapping("/apply")
	public void applyGET(@RequestParam("pno") Long pno , Model model) {
		PolicyDTO policy = policyService.getPolicy(pno); // 신청 대상 정책 상세 조회
		model.addAttribute("policy", policy); // 화면에 정책 요약 표시용으로 전달
	}
	

	// 신청서 제출 처리 (첨부파일 저장은 UpDownController에 실제로 위임함)
	@PostMapping("/apply")
	public String applyPOST(ApplicationApplyDTO dto, UploadFileDTO uploadFileDTO, HttpSession session, RedirectAttributes rttr) {

		String loginUserId = (String) session.getAttribute("loginUser");

		//비회원인 상태로 진입 시 로그인 페이지로 이동
		if (loginUserId == null) {
			return "redirect:/user/login"; // 기존 코드의 오타(/member/login)를 실제 로그인 경로로 수정
		}

		//서버에서 작성자 주입 후 신청 등록, 첨부파일을 이어붙일 수 있도록 생성된 신청번호를 받음
		Long appNo = applicationService.applyPolicy(dto, loginUserId);

		int attemptedCount = countNonEmptyFiles(uploadFileDTO); // 실제로 선택된 파일 개수 (일부 실패 여부 판단 기준)

		List<UploadResultDTO> results = upDownController.upload(uploadFileDTO); // 파일 저장은 UpDownController가 전담 (반복/예외처리 모두 그쪽 책임)

		for (UploadResultDTO result : results) { // 저장에 성공한 파일들만 신청에 연결
			applicationService.addFile(appNo, result.getSavedName(), result.getOriginalName());
		}

		if (results.size() < attemptedCount) { // 선택한 파일 수보다 저장 성공 개수가 적다면 일부 실패한 것
			rttr.addFlashAttribute("error", "일부 첨부파일 업로드에 실패했습니다. 신청은 정상 접수되었습니다."); // 안내 메시지
		} else {
			rttr.addFlashAttribute("result", "정책 신청이 완료되었습니다."); // 성공 메시지 전달
		}

		return "redirect:/application/list";
	}

	// 업로드 폼에서 실제로 선택된(빈 칸이 아닌) 파일 개수를 세는 헬퍼 메서드 - 일부 업로드 실패 여부 판단에 사용
	private int countNonEmptyFiles(UploadFileDTO uploadFileDTO) {
		if (uploadFileDTO.getFiles() == null) { // 아예 선택하지 않았다면
			return 0;
		}
		int count = 0; // 실제 선택된 파일 개수
		for (MultipartFile file : uploadFileDTO.getFiles()) { // 선택된 파일마다 확인
			if (file != null && !file.isEmpty()) { // 빈 입력(선택 안 한 칸)이 아니라면
				count++;
			}
		}
		return count;
	}

	// 내 신청 목록 화면 (페이징)
	@GetMapping("/list")
	public void myApplicationList(HttpSession session, PageRequestDTO pageRequestDTO, Model model) {
		String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 회원 아이디

		if (loginUserId == null) { // 비로그인 상태라면
			model.addAttribute("error", "로그인이 필요합니다."); // 화면에서 안내 문구를 보여주도록 전달
			return; // 목록 조회 없이 종료
		}

		PageResponseDTO<ApplicationDTO> responseDTO = applicationService.getMyApplications(loginUserId, pageRequestDTO); // 내 신청 목록+페이징 조회
		model.addAttribute("responseDTO", responseDTO); // 뷰에 전달
		model.addAttribute("pageRequestDTO", pageRequestDTO); // 페이지네이션 링크 생성을 위해 전달
	}

	// 신청 상세 화면 (본인 신청만 조회 가능, 첨부파일 목록도 함께 표시)
	@GetMapping("/read")
	public void applicationDetail(Long appNo, HttpSession session, Model model) {
		String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 회원 아이디

		if (loginUserId == null) { // 비로그인 상태라면
			model.addAttribute("error", "로그인이 필요합니다."); // 화면에서 안내 문구를 보여주도록 전달
			return; // 조회 없이 종료
		}

		try {
			ApplicationDTO application = applicationService.getApplication(appNo, loginUserId); // 소유자 검증 포함 상세 조회
			model.addAttribute("dto", application); // 뷰에 전달

			List<ApplicationFileDTO> fileList = applicationService.getFiles(appNo); // 첨부파일 목록 조회
			model.addAttribute("fileList", fileList); // 뷰에 전달
		} catch (IllegalStateException e) { // 존재하지 않거나 본인 소유가 아닌 경우
			model.addAttribute("error", e.getMessage()); // 화면에 오류 메시지 표시 (void 반환이라 리다이렉트 대신 에러 블록으로 처리)
		}
	}

	// 첨부파일 다운로드 (본인 또는 관리자만 가능 - /uploads/**로 누구나 내려받던 것을 대체)
	@GetMapping("/download")
	public void download(Long fileNo, HttpSession session, HttpServletResponse response) throws IOException {
		String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 회원 아이디
		Boolean loginRole = (Boolean) session.getAttribute("loginRole"); // 관리자 여부

		if (loginUserId == null) { // 비로그인 상태라면
			response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403 응답
			return;
		}

		ApplicationFileDTO file;
		try {
			file = applicationService.getFile(fileNo); // 파일 번호로 첨부파일 정보 조회
		} catch (IllegalStateException e) { // 존재하지 않는 파일이라면
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 응답
			return;
		}

		ApplicationDTO application;
		try {
			application = applicationService.getApplicationForFile(file.getAppNo()); // 파일이 속한 신청 정보 조회 (소유자 확인용)
		} catch (IllegalStateException e) { // 신청 내역이 존재하지 않는다면 (정상적으로는 발생하지 않음)
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 응답
			return;
		}

		boolean isOwner = application.getUserId().equals(loginUserId); // 본인 신청인지 확인
		boolean isAdmin = Boolean.TRUE.equals(loginRole); // 관리자인지 확인

		if (!isOwner && !isAdmin) { // 본인도 관리자도 아니라면
			response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403 응답 (다른 사람 첨부파일 접근 차단)
			return;
		}

		Path filePath = Paths.get(uploadDir).resolve(file.getSavedName()); // 실제 저장 경로
		if (!Files.exists(filePath)) { // 파일이 실제로 존재하지 않는다면
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 응답
			return;
		}

		response.setContentType("application/octet-stream"); // 브라우저가 직접 열지 않고 다운로드하도록 지정
		response.setHeader("Content-Disposition", // 다운로드 시 저장될 파일명 지정 (한글 파일명 대응을 위해 UTF-8 인코딩)
				"attachment; filename=\"" + URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8) + "\"");

		Files.copy(filePath, response.getOutputStream()); // 파일 내용을 응답 스트림으로 그대로 전송
		response.getOutputStream().flush(); // 남은 버퍼 즉시 전송
	}

	// 신청 취소 처리 (본인 신청만 취소 가능)
	@PostMapping("/cancel")
	public String cancelApplication(Long appNo, HttpSession session, RedirectAttributes rttr) {
		String loginUserId = (String) session.getAttribute("loginUser"); // 로그인한 회원 아이디

		if (loginUserId == null) { // 비로그인 상태라면
			return "redirect:/user/login"; // 로그인 화면으로 이동
		}

		try {
			applicationService.cancel(appNo, loginUserId); // 소유자 검증 포함 취소 처리
			rttr.addFlashAttribute("result", "신청이 취소되었습니다."); // 성공 메시지
		} catch (IllegalStateException e) { // 존재하지 않거나 본인 소유가 아니거나 이미 취소된 경우
			rttr.addFlashAttribute("error", e.getMessage()); // 실패 사유 전달
		}

		return "redirect:/application/read?appNo=" + appNo; // 취소 처리한 신청 상세 화면으로 되돌아감
	}
}
