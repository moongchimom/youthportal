package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter/setter 등 일괄 생성
@Builder // 빌더 패턴 사용 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class PolicyDTO { // 정책 목록/상세/등록/수정 화면에서 사용하는 DTO (supportContent는 사용자 확인에 따라 제외)
	private Long pno; // 정책 번호
	private String sourceId; // 정책 출처 ID (외부 API의 plcyNo, 20자리라 Long 범위를 넘어서 String으로 저장)
	private String title; // 정책 제목
	private String content; // 정책 내용(본문)
	private String category; // 정책 분류
	private String region; // 지역구명
	private String targetAge; // 지원 대상 연령
	private LocalDate startDate; // 지원 시작일 (사업기간)
	private LocalDate endDate; // 지원 종료일 (사업기간)
	private LocalDate applyStartDate; // 신청 시작일 (신청기간) - "마감" 여부는 이 필드 기준으로 판단
	private LocalDate applyEndDate; // 신청 마감일 (신청기간) - "마감" 여부는 이 필드 기준으로 판단
	private LocalDateTime regDate; // 등록일시

	// 신청기간이 지났는지 여부 (DB에 저장하지 않고 화면에 보여줄 때 그때그때 계산 - 화면의 "마감" 뱃지용)
	public boolean isFinished() {
		return applyEndDate != null && applyEndDate.isBefore(LocalDate.now());
	}
}
