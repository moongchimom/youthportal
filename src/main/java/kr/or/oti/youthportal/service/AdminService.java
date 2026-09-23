package kr.or.oti.youthportal.service; // 서비스(비즈니스 로직) 패키지

import kr.or.oti.youthportal.dto.AdminMemberModifyDTO;
import kr.or.oti.youthportal.dto.ApplicationDTO;
import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;
import kr.or.oti.youthportal.dto.UserDTO;

public interface AdminService { // 관리자 전용 비즈니스 로직 인터페이스

	PageResponseDTO<UserDTO> getMemberList(PageRequestDTO pageRequestDTO); // 회원 목록 조회 (페이징+검색)

	UserDTO getMember(String userId); // 회원 상세 조회

	void modifyMember(AdminMemberModifyDTO dto); // 회원 정보 수정 (탈퇴/잠금/실패횟수 초기화)

	PageResponseDTO<ApplicationDTO> getApplicationList(PageRequestDTO pageRequestDTO); // 전체 신청 목록 조회 (페이징+검색)

	void updateApplicationStatus(Long appNo, String status); // 신청 상태 변경 (승인/반려)

}
