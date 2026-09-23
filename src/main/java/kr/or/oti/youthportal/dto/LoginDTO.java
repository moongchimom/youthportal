package kr.or.oti.youthportal.dto; // DTO 패키지 선언

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 데이터 클래스용 편의 어노테이션 (getter/setter 등 일괄 생성)
@Builder // LoginDTO.builder()...build() 형태로 객체 생성 가능하게 함
@NoArgsConstructor // 폼 바인딩(스프링 MVC)을 위한 기본 생성자 생성
@AllArgsConstructor // 전체 필드를 받는 생성자 생성 (Builder가 내부적으로 사용)
public class LoginDTO { // 로그인 화면 입력값을 담는 DTO
	private String userId; // 입력한 아이디
	private String upw; // 입력한 비밀번호
}
