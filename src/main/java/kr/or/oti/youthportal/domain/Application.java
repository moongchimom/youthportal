package kr.or.oti.youthportal.domain; // 도메인(엔티티) 클래스들이 위치하는 패키지

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//신청서 정보

@Getter // 모든 필드에 대한 getter 생성
@Setter // 모든 필드에 대한 setter 생성 (MyBatis 결과 매핑용)
@Builder // Application.builder()....build() 형태로 객체 생성 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class Application { // 정책 신청 도메인 엔티티 (첨부파일은 여러 개일 수 있어 ApplicationFile로 분리)

	private Long appNo; // 신청 번호 (기본키, IDENTITY 자동채번)
	private Long pno; // 신청한 정책 번호 (외래키)
	private String userId; // 신청한 회원 아이디 (외래키)
	private String reason; // 신청 사유
	private String status; // 신청 상태 (접수중/승인/반려/취소)
	private LocalDateTime applyDate; // 신청일시
	private LocalDateTime updateDate; // 상태 변경일시
}
