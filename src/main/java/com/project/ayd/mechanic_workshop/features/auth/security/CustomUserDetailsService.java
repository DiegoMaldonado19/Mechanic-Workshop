package com.project.ayd.mechanic_workshop.features.auth.security;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return createUserDetails(user);
    }

    private UserDetails createUserDetails(User user) {
        List<GrantedAuthority> authorities = getAuthorities(user);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.getIsActive())
                .accountLocked(!user.isAccountNonLocked())
                .authorities(authorities)
                .build();
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        String roleName = "ROLE_" + user.getUserType().getName().toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
}