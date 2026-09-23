package kr.or.oti.youthportal.domain; // 도메인(엔티티) 클래스들이 위치하는 패키지

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//지원정책 정보

@Getter // 모든 필드에 대한 getter 생성
@Setter // 모든 필드에 대한 setter 생성 (MyBatis 결과 매핑용)
@Builder // Policy.builder()....build() 형태로 객체 생성 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class Policy { // 청년정책 도메인 엔티티 (supportContent는 사용자 확인에 따라 제외)
	private Long pno; // 정책 번호 (기본키, IDENTITY 자동채번)
	private String sourceId; // 정책 출처 ID (외부 API의 plcyNo, 20자리라 Long 범위를 넘어서 String으로 저장)
	private String title; // 정책 제목
	private String content; // 정책 내용(본문)
	private String category; // 정책 분류 (일자리/주거/교육 등)
	private String region; // 지역구명 (예: "전남광주통합특별시")
	private String targetAge; // 지원 대상 연령 (예: "만 19세~34세")
	private LocalDate startDate; // 정책 지원 시작일 (사업기간)
	private LocalDate endDate; // 정책 지원 종료일 (사업기간)
	private LocalDate applyStartDate; // 신청 시작일 (신청기간) - "마감" 여부는 이 필드 기준으로 판단
	private LocalDate applyEndDate; // 신청 마감일 (신청기간) - "마감" 여부는 이 필드 기준으로 판단
	private LocalDateTime regDate; // 정책 등록일시
}
