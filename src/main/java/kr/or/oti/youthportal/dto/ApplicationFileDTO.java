package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter/setter 등 일괄 생성
@Builder // 빌더 패턴 사용 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class ApplicationFileDTO { // APPLICATION_FILE 테이블 1행 = 이 객체 1개 (저장/조회 양쪽에 공용으로 사용)
	private Long fileNo; // 파일 번호 (기본키, IDENTITY 자동채번)
	private Long appNo; // 소속 신청 번호 (다운로드 시 소유자 확인용)
	private String savedName; // 서버 저장 파일명 (UUID_원본명, 중복 방지)
	private String originalName; // 원본 파일명 (화면 노출/다운로드명)
	private LocalDateTime uploadDate; // 업로드일시 (저장 시에만 사용, 목록 조회 쿼리에서는 뽑지 않음)
}
