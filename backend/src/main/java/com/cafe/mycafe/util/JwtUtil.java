package com.cafe.mycafe.util;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;


@Component
public class JwtUtil {

	private final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
	/*
	 * custom.properties 파일을 만들고 해당 파일에
	 * jwt.secret 라는 키 값으로 토큰을 발급 할 때 서명할 key를 설정했는데 그 문자열을 읽어와야 한다.
	 * */

	@Value("${jwt.secret}")
	private String secretKey;

	//jwt.expiration 라는 키 값으로 토큰의 유효기간을 설정했는데 해당 숫자를 읽어와서 사용한다.
	@Value("${jwt.expiration}")
	private long expiration;

	private Key getSigningKey() {
		//return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
		byte[] keyBytes = Base64.getDecoder().decode(secretKey);
		return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser()
				.setSigningKey(getSigningKey()) // token 발급시 서명했던 키값도 일치하는지 확인도 된다.
				.parseClaimsJws(token)
				.getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/*
	 * userName과 추가 정보(Claims)를 전달하면 해당 정보를 token에 저장하고
	 * 만들어진 token문자열을 리턴하는 메서드
	 * */
	public String generateAccessToken(String username, Map<String, Object> extraClaims) {
		Map<String, Object> claims = new HashMap<>(extraClaims);
		return createToken(claims, username);
	}

	public String generateRefreshToken(String username, Map<String, Object> extraClaims) {
		Map<String, Object> claims = new HashMap<>(extraClaims);
		return createToken(claims, username);
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims) // 추가 정보
				.setSubject(subject) // 주요 정보(주로 userName)
				.setIssuer("your-issuer") // 추가된 issuer(발급한 서비스명)
				.setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간
				.setExpiration(new Date(System.currentTimeMillis() + expiration)) // 파기되는 시간
				.signWith(SignatureAlgorithm.HS256, getSigningKey()) // HS256 알고리즘으로 서명
				// SignatureAlgorithm.HS256
				.compact();
	}

	// token 검증하는 메소드
	public boolean validateToken(String token) {
		// token 에 담긴 추가 정보를 얻어낼수 있다. (role, issuer등등)
		Claims claims = extractAllClaims(token);
		// 토큰 유효기간이 남아 있는지와 issuer 정보도 일치하는지 확인해서
		boolean isValid = !isTokenExpired(token) && "your-issuer".equals(claims.getIssuer());
		// 유효성 여부를 리턴한다.
		return isValid;
	}


	// Refresh Token 꺼내오기
	public String resolveRefreshToken(HttpServletRequest request) {
		if (request.getCookies() == null) return null;

		for (Cookie cookie : request.getCookies()) {
			if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
