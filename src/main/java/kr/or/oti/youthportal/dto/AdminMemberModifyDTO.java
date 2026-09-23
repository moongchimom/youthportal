package kr.or.oti.youthportal.dto; // DTO 패키지 선언

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 데이터 클래스용 편의 어노테이션 (getter/setter 등 일괄 생성)
@Builder // AdminMemberModifyDTO.builder()...build() 형태로 객체 생성 가능하게 함
@NoArgsConstructor // 폼 바인딩(스프링 MVC)을 위한 기본 생성자 생성
@AllArgsConstructor // 전체 필드를 받는 생성자 생성 (Builder가 내부적으로 사용)
public class AdminMemberModifyDTO { // 관리자의 회원 상태 변경 화면 입력값을 담는 DTO
	private String userId; // 대상 회원 아이디
	private boolean del; // 탈퇴 처리 여부
	private boolean resetFailCount; // 로그인 실패 횟수 초기화 여부
	private boolean accountLocked; // 계정 잠금 여부
}
