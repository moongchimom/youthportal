package kr.or.oti.youthportal.dto.api;

import lombok.Data;

// 실제 응답(JSON) 필드 중 우리가 쓰는 것만 옮겨 담음. 선언 안 한 필드는 자동으로 무시됨.
@Data
public class YouthPolicyApiItemDTO {

	private String plcyNo;          // 정책 고유번호 (추후 중복방지/upsert 시 SOURCE_ID로 활용 예정, 아직 미사용)
	private String plcyNm;          // 정책명 -> title
	private String plcyExplnCn;     // 정책 설명 -> content
	private String lclsfNm;         // 정책 대분류명 (예: "일자리") -> category
	private String sprvsnInstCdNm;  // 주관기관명 (예: "전남광주통합특별시") -> region (지역구명으로 사용)
	private String pvsnInstGroupCd; // 기관 그룹 코드 (0054001=중앙부처, 그 외=지자체/산하기관 - 온통청년 자체 지역필터가 쓰는 것과 동일한 코드) -> region 판별에 사용
	private String sprtTrgtMinAge;  // 지원 대상 최소 연령
	private String sprtTrgtMaxAge;  // 지원 대상 최대 연령
	private String bizPrdBgngYmd;   // 사업기간 시작일 (yyyyMMdd, "연중"인 경우 공백으로 옴) -> startDate
	private String bizPrdEndYmd;    // 사업기간 종료일 (yyyyMMdd, "연중"인 경우 공백으로 옴) -> endDate
	private String aplyYmd;         // 신청기간 (예: "20260312 ~ 20260319", 상시면 공백) -> applyStartDate/applyEndDate, 온통청년이 "마감" 여부를 판단하는 실제 기준

	// 주의: plcySprtCn(지원내용)은 PolicyDTO의 supportContent에 해당 - 의도적으로 매핑하지 않음
}
