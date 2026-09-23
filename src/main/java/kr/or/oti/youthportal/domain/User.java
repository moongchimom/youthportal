package kr.or.oti.youthportal.domain; // 도메인(엔티티) 클래스들이 위치하는 패키지

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//사용자정보

@Getter // 모든 필드에 대한 getter 생성
@Setter // 모든 필드에 대한 setter 생성 (MyBatis 결과 매핑용)
@Builder // User.builder()....build() 형태로 객체 생성 가능
@NoArgsConstructor // 기본 생성자 생성 (MyBatis가 객체를 먼저 만들고 setter로 값을 채움)
@AllArgsConstructor // 전체 필드 생성자 생성 (Builder 내부에서 사용)
public class User { // 회원(사용자) 도메인 엔티티
	private String userId; // 회원 아이디 (기본키)
	private String upw; // 회원 비밀번호
	private String name; // 회원 이름
	private String birthDate; // 생년월일 (yyyyMMdd 문자열)
	private String phone; // 휴대폰 번호
	private LocalDateTime regDate; // 가입일시
	private boolean role; // 권한 (false=일반회원, true=관리자)
	private boolean del; // 탈퇴 여부 (false=정상, true=탈퇴)
	private int failCount; // 로그인 실패 누적 횟수
	private boolean accountLocked; // 계정 잠금 여부 (실패 누적 시 true)
	private String socialType; // 소셜 로그인 구분 (null=일반가입, "KAKAO"=카카오 로그인)
	private String socialId; // 소셜 로그인 제공자가 발급한 회원 고유번호
}
