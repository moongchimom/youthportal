package kr.or.oti.youthportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// PasswordEncoder를 SecurityConfig가 아닌 별도 클래스에 두는 이유:
// KakaoOAuth2UserService가 PasswordEncoder를 생성자로 주입받는데, SecurityConfig도 KakaoOAuth2UserService를 주입받다 보니
// PasswordEncoder 빈이 SecurityConfig 안에 있으면 SecurityConfig <-> KakaoOAuth2UserService 순환 참조가 생김
@Configuration
public class PasswordEncoderConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
