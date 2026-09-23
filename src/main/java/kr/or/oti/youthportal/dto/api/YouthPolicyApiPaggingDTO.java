package kr.or.oti.youthportal.dto.api;

import lombok.Data;

// 응답의 result.pagging 부분 - 전체 건수 확인용 (여러 페이지 순회 기능은 아직 없음)
@Data
public class YouthPolicyApiPaggingDTO {

	private int totCount; // 전체 정책 건수
	private int pageNum;  // 현재 페이지 번호
	private int pageSize; // 페이지당 건수
}
