package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.domain.User;
import org.smarteye.backend.repository.UserRepository;
import org.smarteye.backend.security.model.Role;
import org.smarteye.backend.security.model.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ==== Spring Security ====

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        var authorities = List.of(new SimpleGrantedAuthority(u.getRole().name()));
        return UserPrincipal.builder()
                .id(u.getId())
                .username(u.getUsername())
                .password(u.getPassword())
                .enabled(u.isEnabled())
                .authorities(authorities)
                .build();
    }

    // ==== CRUD ====

    @Transactional(readOnly = true)
    public User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: id=" + id));
    }

    public User create(String username, String rawPassword, Role role) {
        User u = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role != null ? role : Role.OPERATOR)
                .enabled(true)
                .build();
        return userRepository.save(u);
    }

    public User update(Long id, String username, Role role, Boolean enabled) {
        User u = getOrThrow(id);
        if (username != null) u.setUsername(username);
        if (role != null) u.setRole(role);
        if (enabled != null) u.setEnabled(enabled);
        return u;
    }

    public void changePassword(Long id, String newRawPassword) {
        User u = getOrThrow(id);
        u.setPassword(passwordEncoder.encode(newRawPassword));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
