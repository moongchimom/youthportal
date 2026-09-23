package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter/setter 등 일괄 생성
@Builder // 빌더 패턴 사용 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class ApplicationDTO { // 신청 목록/상세 화면에서 사용하는 DTO (신청+정책+회원 조인 결과)
	private Long appNo; // 신청 번호
	private Long pno; // 신청한 정책 번호
	private String policyTitle; // 신청한 정책 제목 (POLICY 조인)
	private String userId; // 신청한 회원 아이디
	private String userName; // 신청한 회원 이름 (USERS 조인)
	private String status; // 신청 상태 (접수중/승인/반려/취소)
	private LocalDateTime applyDate; // 신청일시
	private LocalDateTime updateDate; // 상태 변경일시
	private String reason; // 신청 사유 (상세 화면 노출용 확장 필드)
}
