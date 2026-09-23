package kr.or.oti.youthportal.service; // 서비스(비즈니스 로직) 패키지

import java.util.List;

import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;
import kr.or.oti.youthportal.dto.PolicyDTO;

public interface PolicyService { // 정책 관련 비즈니스 로직 인터페이스

	PageResponseDTO<PolicyDTO> getList(PageRequestDTO pageRequestDTO); // 정책 목록 조회 (페이징+검색)

	List<String> getRegionList(); // 검색폼 지역구 라디오 버튼용 목록

	PolicyDTO getPolicy(Long pno); // 정책 상세 조회

	void registerPolicy(PolicyDTO dto); // 정책 등록 (관리자)

	void modifyPolicy(PolicyDTO dto); // 정책 수정 (관리자)

	void removePolicy(Long pno); // 정책 삭제 (관리자)

	int importFromApi();
	
}
