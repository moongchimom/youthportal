package kr.or.oti.youthportal.dto.api;

import java.util.List;

import lombok.Data;

// 응답의 result 부분 - pagging과 실제 정책 목록(youthPolicyList)을 함께 담음
@Data
public class YouthPolicyApiResultDTO {

	private YouthPolicyApiPaggingDTO pagging;
	private List<YouthPolicyApiItemDTO> youthPolicyList;
}
