package org.smarteye.backend.security.api;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.security.jwt.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginResponse(String token, String tokenType, String username, java.util.List<String> roles) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest body) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.username(), body.password()));
        var principal = auth.getName();
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::valueOf)
                .collect(Collectors.toList());

        var token = jwtService.generate(principal, roles);   // <- строки
        return ResponseEntity.ok(new LoginResponse(token, "Bearer", principal, roles));
    }

    /** Текущий пользователь (по JWT). */
    @GetMapping("/me")
    public LoginResponse me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth != null ? auth.getName() : "anonymous";
        List<String> roles = (auth != null)
                ? auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::valueOf)
                .collect(Collectors.toList())
                : java.util.Collections.<String>emptyList();
        return new LoginResponse(null, "Bearer", principal, roles);
    }
}
