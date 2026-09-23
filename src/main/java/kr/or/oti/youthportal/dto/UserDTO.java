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
public class UserDTO { // 로그인 결과 및 관리자 화면에서 사용하는 회원 정보 DTO
	private String userId; // 회원 아이디
	private String upw; // 비밀번호 (로그인 성공 시 컨트롤러/서비스에서 null 처리하여 화면 노출 방지)
	private String name; // 이름
	private String birthDate; // 생년월일
	private String phone; // 휴대폰 번호
	private LocalDateTime regDate; // 가입일시 (관리자 회원목록 화면용 확장 필드)
	private boolean role; // 권한 (false=일반회원, true=관리자, 세션 저장에도 사용)
	private boolean del; // 탈퇴 여부 (관리자 회원목록 화면용 확장 필드)
	private int failCount; // 로그인 실패 누적 횟수 (로그인 잠금 로직 + 관리자 회원목록 화면용 확장 필드)
	private boolean accountLocked; // 계정 잠금 여부 (로그인 잠금 로직 + 관리자 회원목록 화면용 확장 필드)
	private String socialType; // 소셜 로그인 구분 (null=일반가입, "KAKAO"=카카오 로그인)
	private String socialId; // 소셜 로그인 제공자가 발급한 회원 고유번호
}
