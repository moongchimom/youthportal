package kr.or.oti.youthportal.service; // 서비스(비즈니스 로직) 패키지

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.oti.youthportal.domain.Application;
import kr.or.oti.youthportal.domain.ApplicationFile;
import kr.or.oti.youthportal.dto.ApplicationApplyDTO;
import kr.or.oti.youthportal.dto.ApplicationDTO;
import kr.or.oti.youthportal.dto.ApplicationFileDTO;
import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;
import kr.or.oti.youthportal.mapper.ApplicationDAO;
import kr.or.oti.youthportal.mapper.ApplicationFileDAO;
import lombok.RequiredArgsConstructor;

@Service // 스프링이 이 클래스를 서비스 빈으로 등록
@RequiredArgsConstructor // applicationDAO, applicationFileDAO를 주입받는 생성자를 자동 생성
public class ApplicationServiceImpl implements ApplicationService{

	private final ApplicationDAO applicationDAO; // 신청 관련 SQL을 수행하는 매퍼
	private final ApplicationFileDAO applicationFileDAO; // 신청 첨부파일 관련 SQL을 수행하는 매퍼

	@Override
	@Transactional
	public Long applyPolicy(ApplicationApplyDTO dto, String userId) {

		Application application = Application.builder() // 신청 DTO + 로그인 아이디 -> 도메인 엔티티 변환
				.pno(dto.getPno()) // 신청 대상 정책 번호
				.userId(userId) // 서버에서 주입한 로그인 아이디 (클라이언트 값 신뢰하지 않음)
				.reason(dto.getReason()) // 신청 사유
				.status("접수중") // 신청 초기 상태
				.applyDate(LocalDateTime.now()) // 신청일시는 서버 시간으로 고정
				.build(); // 객체 생성

		applicationDAO.insertApplication(application); // DB에 저장 (성공 시 application.appNo에 채번된 값이 채워짐)

		return application.getAppNo(); // 컨트롤러가 첨부파일을 이어서 저장할 수 있도록 생성된 신청 번호 반환
	}

	@Override
	public void addFile(Long appNo, String savedName, String originalName) {
		ApplicationFile file = ApplicationFile.builder() // 첨부파일 정보를 도메인 엔티티로 변환
				.appNo(appNo) // 소속 신청 번호
				.savedName(savedName) // 서버 저장 파일명
				.originalName(originalName) // 원본 파일명
				.uploadDate(LocalDateTime.now()) // 업로드일시는 서버 시간으로 고정
				.build(); // 객체 생성

		applicationFileDAO.insertFile(file); // DB에 저장
	}

	@Override
	public PageResponseDTO<ApplicationDTO> getMyApplications(String userId, PageRequestDTO pageRequestDTO){
		int total = applicationDAO.selectCountByUserId(userId); // 해당 회원의 전체 신청 건수 조회
		var applications = applicationDAO.selectByUserId(userId, pageRequestDTO); // 해당 회원의 신청 목록 조회 (페이징)
		return PageResponseDTO.of(pageRequestDTO, total, applications); // 페이징 정보와 함께 응답 조립
	}

	@Override
	public ApplicationDTO getApplication(Long appNo, String userId) {
		ApplicationDTO application = applicationDAO.selectById(appNo); // 신청 번호로 상세 조회

		if (application == null) { // 존재하지 않는 신청이라면
			throw new IllegalStateException("존재하지 않는 신청 내역입니다."); // 조회 중단
		}

		if (!application.getUserId().equals(userId)) { // 로그인한 회원의 신청이 아니라면
			throw new IllegalStateException("본인의 신청 내역만 조회할 수 있습니다."); // 조회 중단 (소유자 검증)
		}

		return application; // 소유자 검증을 통과한 신청 상세
	}

	@Override
	@Transactional
	public void cancel(Long appNo, String userId) {
		ApplicationDTO application = applicationDAO.selectById(appNo); // 취소 대상 신청 조회

		if (application == null) { // 존재하지 않는 신청이라면
			throw new IllegalStateException("존재하지 않는 신청 내역입니다."); // 취소 중단
		}

		if (!application.getUserId().equals(userId)) { // 로그인한 회원의 신청이 아니라면
			throw new IllegalStateException("본인의 신청 내역만 취소할 수 있습니다."); // 취소 중단 (소유자 검증)
		}

		if ("취소".equals(application.getStatus())) { // 이미 취소된 신청이라면
			throw new IllegalStateException("이미 취소된 신청입니다."); // 중복 취소 방지
		}

		applicationDAO.updateStatus(appNo, "취소"); // 상태를 취소로 변경
	}

	@Override
	public ApplicationDTO getApplicationForFile(Long appNo) {
		ApplicationDTO application = applicationDAO.selectById(appNo); // 신청 번호로 조회

		if (application == null) { // 존재하지 않는 신청이라면
			throw new IllegalStateException("존재하지 않는 신청 내역입니다."); // 다운로드 중단 (소유자/관리자 여부 판단은 컨트롤러가 수행)
		}

		return application; // 소유자 검증 없이 반환 - 관리자도 첨부파일을 내려받아야 하므로
	}

	@Override
	public List<ApplicationFileDTO> getFiles(Long appNo) {
		return applicationFileDAO.selectByAppNo(appNo); // 해당 신청에 첨부된 파일 목록 조회
	}

	@Override
	public ApplicationFileDTO getFile(Long fileNo) {
		ApplicationFileDTO file = applicationFileDAO.selectOne(fileNo); // 파일 번호로 조회

		if (file == null) { // 존재하지 않는 파일이라면
			throw new IllegalStateException("존재하지 않는 첨부파일입니다."); // 다운로드 중단
		}

		return file; // 소유자 검증은 컨트롤러가 appNo 기준으로 수행
	}

}
