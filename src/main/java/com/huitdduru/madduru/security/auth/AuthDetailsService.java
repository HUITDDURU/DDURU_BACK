package com.huitdduru.madduru.security.auth;

import com.huitdduru.madduru.exception.exceptions.UserNotFoundException;
import com.huitdduru.madduru.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public AuthDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmail(userEmail)
                .map(AuthDetails::new)
                .orElseThrow(UserNotFoundException::new);
    }
}
