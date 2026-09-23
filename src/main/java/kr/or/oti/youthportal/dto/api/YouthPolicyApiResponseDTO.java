package kr.or.oti.youthportal.dto.api;

import lombok.Data;

// 실제 응답 구조 확인 완료: { resultCode, resultMessage, result: { pagging, youthPolicyList } } (JSON)
@Data
public class YouthPolicyApiResponseDTO {

	private int resultCode;          // 200 = 성공
	private String resultMessage;    // 결과 메시지
	private YouthPolicyApiResultDTO result; // pagging + 정책 목록
}
