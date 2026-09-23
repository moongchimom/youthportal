package kr.or.oti.youthportal.domain; // 도메인(엔티티) 클래스들이 위치하는 패키지

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//신청 첨부파일 정보 (신청 1건에 여러 개 첨부 가능하도록 별도 테이블로 분리)

@Getter // 모든 필드에 대한 getter 생성
@Setter // 모든 필드에 대한 setter 생성 (MyBatis 결과 매핑용)
@Builder // ApplicationFile.builder()....build() 형태로 객체 생성 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class ApplicationFile { // 신청 첨부파일 도메인 엔티티
	private Long fileNo; // 파일 번호 (기본키, IDENTITY 자동채번)
	private Long appNo; // 소속 신청 번호 (외래키)
	private String savedName; // 서버 저장 파일명 (UUID_원본명, 중복 방지)
	private String originalName; // 업로드 당시 원본 파일명 (다운로드 시 사용자에게 보여줄 이름)
	private LocalDateTime uploadDate; // 업로드일시
}
