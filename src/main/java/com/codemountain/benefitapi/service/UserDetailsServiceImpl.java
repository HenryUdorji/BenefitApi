package com.codemountain.benefitapi.service;


import com.codemountain.benefitapi.entities.User;
import com.codemountain.benefitapi.exception.ApiException;
import com.codemountain.benefitapi.repository.UserRepository;
import com.codemountain.benefitapi.security.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "User not found with this username: " + username));
        return UserPrincipal.create(user);
    }


}
