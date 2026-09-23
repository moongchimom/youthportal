package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter/setter 등 일괄 생성
@Builder // 빌더 패턴 사용 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class ApplicationFileDTO { // 신청 상세/다운로드 화면에서 사용하는 첨부파일 DTO
	private Long fileNo; // 파일 번호
	private Long appNo; // 소속 신청 번호 (다운로드 시 소유자 확인용)
	private String savedName; // 서버 저장 파일명
	private String originalName; // 원본 파일명 (화면 노출/다운로드명)
}
