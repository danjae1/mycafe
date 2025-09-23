package com.cafe.mycafe.config;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.cafe.mycafe.service.CustomUserDetailsService;
import com.cafe.mycafe.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired CustomUserDetailsService service;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String jwtToken="";
	
		//요청의 Header 에 "Authorization" 이라는 키값으로 전달된 문자열이 있는지 읽어와 본다.
		String authHeader=request.getHeader("Authorization");
		if(authHeader != null && authHeader.startsWith("Bearer ")) {
			jwtToken = URLDecoder.decode(authHeader, StandardCharsets.UTF_8);
		}
		
		String userName=null;
		if(jwtToken.startsWith("Bearer ")) {
			// "Bearer " 를 제외한 뒤의 token 문자열을 얻어낸다.
			jwtToken = jwtToken.substring(7);
			// userName 을 token 으로 부터 얻어낸다.
			userName= jwtUtil.extractUsername(jwtToken);
		}
		
		//userName 이 존재하고  Spring Security 에서 아직 인증을 받지 않은 상태라면 
		if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			//토큰이 유효한 토큰인지 체크한다음 
			boolean isValid=jwtUtil.validateToken(jwtToken);
			//유효하다면 1회성 로그인(spring security 를 통과할 로그인) 을 시켜준다.
			if(isValid) {
				Claims claims = jwtUtil.extractAllClaims(jwtToken); // JWT에서 모든 정보 가져오기
				String role = claims.get("role", String.class);
				// userName 과 role 정보를 담은 UserDetails 객체를 만든다. 
				UserDetails ud=User.withUsername(userName)
							.password("") //비밀번호는 필요없지만 null 인 상태면 builder 에서 에러발생
							.authorities(role)
							.build();
				//사용자가 제출한 사용자 이름과 비밀번호와 같은 인증 자격 증명을 저장
				UsernamePasswordAuthenticationToken authToken=
					new UsernamePasswordAuthenticationToken(ud, null, 
							ud.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				//Security 컨텍스트 업데이트 (1회성 로그인)
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		//다음 spring 필터 chain 진행하기
		filterChain.doFilter(request, response);	
	} 

}










