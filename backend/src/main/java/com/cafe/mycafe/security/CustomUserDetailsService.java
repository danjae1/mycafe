package com.cafe.mycafe.security;

import java.util.List;


import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService{
	
	private final UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("계정을 찾지 못하였습니다."));

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()));

		return new CustomUserDetails(user);

	}

}
