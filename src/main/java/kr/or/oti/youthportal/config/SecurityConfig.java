package kr.or.oti.youthportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import kr.or.oti.youthportal.security.KakaoOAuth2UserService;
import kr.or.oti.youthportal.security.LoginFailureHandler;
import kr.or.oti.youthportal.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;

// 로그인/회원가입/관리자 권한 체크를 Spring Security로 위임하는 설정 (기존 AdminInterceptor+WebMvcConfig를 대체)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final KakaoOAuth2UserService kakaoOAuth2UserService;
	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;

	// 비밀번호 해시 방식(PasswordEncoder)은 순환 참조를 피하기 위해 PasswordEncoderConfig로 분리함

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// 접근 권한은 전부 여기서 결정 - 컨트롤러 안에서 로그인 여부를 일일이 검사하지 않음
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers("/application/**").authenticated() // 신청 관련 화면은 전부 로그인 필수 (비로그인은 로그인 화면으로 유도됨)
					.anyRequest().permitAll()
			)
			// 일반 로그인 (아이디/비밀번호 폼, 필드명은 login.html의 input name과 맞춤)
			.formLogin(form -> form
					.loginPage("/user/login")
					.loginProcessingUrl("/user/login")
					.usernameParameter("userId")
					.passwordParameter("upw")
					.successHandler(loginSuccessHandler)
					.failureHandler(loginFailureHandler)
					.permitAll()
			)
			// 카카오 소셜 로그인 (인가코드->토큰 교환, 토큰으로 프로필 조회는 스프링 시큐리티가 처리, 우리는 프로필->회원 매핑만 담당)
			.oauth2Login(oauth2 -> oauth2
					.loginPage("/user/login")
					.userInfoEndpoint(userInfo -> userInfo.userService(kakaoOAuth2UserService))
					.successHandler(loginSuccessHandler)
					.failureHandler(loginFailureHandler)
			)
			// 로그아웃 (기존과 동일한 URL/이동 경로 유지 - header.html의 로그아웃 링크가 <a> 태그(GET)라 명시적으로 GET을 허용)
			.logout(logout -> logout
					.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout", "GET"))
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
			)
			// 관리자 아닌 로그인 사용자가 /admin/**에 접근하면 로그인 화면으로 보냄 (기존 AdminInterceptor와 동일한 결과)
			.exceptionHandling(ex -> ex
					.accessDeniedHandler((request, response, e) ->
							response.sendRedirect(request.getContextPath() + "/user/login"))
			);

		return http.build();
	}
}
