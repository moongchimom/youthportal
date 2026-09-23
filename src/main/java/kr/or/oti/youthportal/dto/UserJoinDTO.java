package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter/setter 등 일괄 생성
@Builder // 빌더 패턴 사용 가능
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 전체 필드 생성자 생성
public class UserJoinDTO { // 회원가입 폼에서 전달받는 DTO

	@NotBlank // 아이디는 필수 입력
	private String userId; // 회원 아이디

	@NotBlank // 비밀번호는 필수 입력
	private String upw; // 비밀번호

	@NotBlank // 비밀번호 확인은 필수 입력
	private String upwConfirm; // 비밀번호 확인 (upw와 일치해야 가입 가능)

	@NotBlank // 이름은 필수 입력
	private String name; // 이름

	@NotBlank // 생년월일은 필수 입력
	private String birthDate; // 생년월일

	private String phone; // 휴대폰 번호 (선택 입력)
}
