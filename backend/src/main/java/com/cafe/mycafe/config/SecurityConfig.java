package com.cafe.mycafe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration //설정 클래스라고 알려준다
@EnableWebSecurity //Security 를 설정하기 위한 어노테이션
@EnableMethodSecurity(securedEnabled = true) //Controller 메소드에서 권한 체크 가능 하도록
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;
	/*
	 *  매개변수에 전달되는 HttpSecurity 객체를 이용해서 우리의 프로젝트 상황에 맞는 설정을 기반으로
	 *  만들어진 SecurityFilterChain 객체를 리턴해주어야 한다.
	 *  또한 SecurityFilterChain 객체도 스프링이 관리하는 Bean 이 되어야 한다
	 */
	@Bean //메소드에서 리턴되는 SecurityFilterChain 을 bean 으로 만들어준다.
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
		String[] whiteList= {"/auth","/swagger-ui/**","/v3/api-docs/**","/api/login","/api/notice","/upload/**"};

		httpSecurity
		.headers(header->
			//동일한 origin 에서 iframe 을 사용할수 있도록 설정(default 값은 사용불가)
			header.frameOptions(option->option.sameOrigin()) //SmartEditor 에서 필요함
		)
		.csrf(csrf->csrf.disable())
		.cors(cors ->cors.configurationSource(corsConfigurationSource()))
		.authorizeHttpRequests(config ->
			config
				.requestMatchers(whiteList).permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/staff/**").hasAnyRole("ADMIN", "STAFF")
				.requestMatchers(HttpMethod.POST, "/api/user/**","/api/login").permitAll() //api 회원가입 요청은 받아들이도록
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/board","/api/board/**").permitAll()
				.anyRequest().authenticated()
		)
		.sessionManagement(config ->
			//세션을 사용하지 않도록 설정한다.
			config.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		)
		//JwtFilter 를 Spring Security 필터보다 미리 수행되게 하기
		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		// 설정 정보를 가지고 있는 HttpSecurity 객체의 build() 메소드를 호출해서 리턴되는 객체를 리턴해준다.
		return httpSecurity.build();
	}

	//비밀번호를 암호화 해주는 객체를 bean 으로 만든다.
	@Bean
	PasswordEncoder passwordEncoder() {
		//여기서 리턴해주는 객체도 bean 으로 된다.
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("http://localhost:9000"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true); // 쿠키/Authorization 헤더 허용

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}

