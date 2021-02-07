package com.codemountain.benefitapi.security;

import com.codemountain.benefitapi.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;


public class UserPrincipal implements UserDetails {

	private String username;

	private String password;

	private Collection<? extends GrantedAuthority> authorities;




	public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		this.username  =  username;
		this.password  =  password;
		this.authorities = new ArrayList<>(authorities);

	}


	public static UserPrincipal create(User user) {
		return new
				UserPrincipal(user.getUsername(),
				user.getPassword(),
				Arrays.stream(user.getRole()
						.split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList()));
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
