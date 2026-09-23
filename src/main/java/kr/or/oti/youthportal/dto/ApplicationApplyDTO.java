package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter/setter 등 일괄 생성
@Builder // 빌더 패턴 사용 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class ApplicationApplyDTO { // 정책 신청 폼(신청서 작성)에서 전달받는 DTO (첨부파일은 UploadFileDTO가 별도로 담당)
	private Long pno; // 신청할 정책 번호
	private String reason; // 신청 사유
}
