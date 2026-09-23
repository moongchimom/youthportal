package kr.or.oti.youthportal.service; // 서비스(비즈니스 로직) 패키지

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.oti.youthportal.dto.AdminMemberModifyDTO;
import kr.or.oti.youthportal.dto.ApplicationDTO;
import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;
import kr.or.oti.youthportal.dto.UserDTO;
import kr.or.oti.youthportal.mapper.ApplicationDAO;
import kr.or.oti.youthportal.mapper.UserDAO;
import lombok.RequiredArgsConstructor;


@Service // 스프링이 이 클래스를 서비스 빈으로 등록
@RequiredArgsConstructor // userDAO, applicationDAO를 주입받는 생성자를 자동 생성

public class AdminServiceImpl implements AdminService{

	private final UserDAO userDAO; // 회원 관련 SQL을 수행하는 매퍼
	private final ApplicationDAO applicationDAO; // 신청 관련 SQL을 수행하는 매퍼

	// 관리자가 지정할 수 있는 신청 상태 (취소는 신청자 본인만 가능하므로 제외)
	private static final Set<String> ALLOWED_STATUS = Set.of("접수중", "승인", "반려");

	@Override
	public PageResponseDTO<UserDTO> getMemberList(PageRequestDTO pageRequestDTO) {
		int total = userDAO.selectMemberTotalCount(pageRequestDTO); // 검색조건에 맞는 전체 회원 수 조회
		List<UserDTO> members = userDAO.selectMemberList(pageRequestDTO); // 검색조건+페이징에 맞는 회원 목록 조회 (쿼리에서 UPW를 빼고 가져옴)

		return PageResponseDTO.of(pageRequestDTO, total, members); // 페이징 정보와 함께 응답 조립
	}

	@Override
	public UserDTO getMember(String userId) {
		return userDAO.selectById(userId); // 아이디로 회원 상세 조회 (이미 UserDTO 형태로 반환됨)
	}

	@Override
	@Transactional
	public void modifyMember(AdminMemberModifyDTO dto) {
		UserDTO current = userDAO.selectById(dto.getUserId()); // 현재 회원 정보 조회 (실패횟수 유지 여부 판단용)

		if (current == null) { // 존재하지 않는 회원이라면
			throw new IllegalStateException("존재하지 않는 회원입니다."); // 수정 중단
		}

		UserDTO updateTarget = UserDTO.builder() // DB에 반영할 대상 생성
				.userId(dto.getUserId()) // 수정 대상 아이디
				.del(dto.isDel()) // 탈퇴 여부 (관리자가 선택한 값)
				.accountLocked(dto.isAccountLocked()) // 잠금 여부 (관리자가 선택한 값)
				.failCount(dto.isResetFailCount() ? 0 : current.getFailCount()) // 초기화 체크 시 0, 아니면 기존 값 유지
				.build(); // 객체 생성

		userDAO.updateMemberByAdmin(updateTarget); // DB에 수정 반영
	}

	@Override
	public PageResponseDTO<ApplicationDTO> getApplicationList(PageRequestDTO pageRequestDTO) {
		int total = applicationDAO.selectAllCount(pageRequestDTO); // 검색조건에 맞는 전체 신청 건수 조회
		List<ApplicationDTO> applications = applicationDAO.selectAllApplications(pageRequestDTO); // 검색조건+페이징에 맞는 신청 목록 조회
		return PageResponseDTO.of(pageRequestDTO, total, applications); // 페이징 정보와 함께 응답 조립
	}

	@Override
	@Transactional
	public void updateApplicationStatus(Long appNo, String status) {
		if (!ALLOWED_STATUS.contains(status)) { // 허용되지 않은 상태값이라면
			throw new IllegalArgumentException("허용되지 않은 상태값입니다: " + status); // 처리 중단
		}
		applicationDAO.updateStatus(appNo, status); // DB에 상태 변경 반영
	}
}
