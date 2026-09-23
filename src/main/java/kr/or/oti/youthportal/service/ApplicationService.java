package kr.or.oti.youthportal.service; // 서비스(비즈니스 로직) 패키지

import java.util.List;

import kr.or.oti.youthportal.dto.ApplicationApplyDTO;
import kr.or.oti.youthportal.dto.ApplicationDTO;
import kr.or.oti.youthportal.dto.ApplicationFileDTO;
import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;

public interface ApplicationService { // 정책 신청 관련 비즈니스 로직 인터페이스

	Long applyPolicy(ApplicationApplyDTO dto, String userId); // 정책 신청 (첨부파일 저장을 위해 생성된 신청 번호를 반환)

	void addFile(Long appNo, String savedName, String originalName); // 신청에 첨부파일 1건 연결 (여러 번 호출해 여러 개 첨부)

	PageResponseDTO<ApplicationDTO> getMyApplications(String userId, PageRequestDTO pageRequestDTO); // 내 신청 목록 조회 (페이징)

	ApplicationDTO getApplication(Long appNo, String userId); // 내 신청 상세 조회 (본인 소유만 허용)

	void cancel(Long appNo, String userId); // 내 신청 취소 (본인 소유만 허용)

	ApplicationDTO getApplicationForFile(Long appNo); // 첨부파일 다운로드용 조회 (소유자 검증은 컨트롤러가 관리자 여부까지 포함해 별도 수행)

	List<ApplicationFileDTO> getFiles(Long appNo); // 특정 신청에 첨부된 파일 목록 조회 (상세 화면용)

	ApplicationFileDTO getFile(Long fileNo); // 파일 번호로 단건 조회 (다운로드 엔드포인트용)
}
