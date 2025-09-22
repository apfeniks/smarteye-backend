package org.smarteye.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    // ВАЖНО: три независимых плейсхолдера, без вложений и без смешивания в одной строке
    @Value("${JWT_SECRET:change_me_super_secret_256bit_key_min_32_chars}")
    private String jwtSecret;

    @Value("${JWT_ISSUER:SmartEYE}")
    private String jwtIssuer;

    @Value("${JWT_EXPIRE_MINUTES:120}")
    private long jwtExpireMinutes;

    private SecretKey key;

    @PostConstruct
    void init() {
        byte[] bytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters (256-bit).");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    /** Генерация access-token. */
    public String generate(String username, List<String> roles) {
        var now = OffsetDateTime.now();
        var exp = now.plusMinutes(jwtExpireMinutes);

        return Jwts.builder()
                .subject(username)
                .issuer(jwtIssuer)
                .claim("roles", roles == null ? List.of() : roles)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(exp.toInstant()))
                .signWith(key)
                .compact();
    }

    private Jws<Claims> parse(String token) {
        var parser = Jwts.parser()
                .verifyWith(key)
                .build();
        return parser.parseSignedClaims(token);
    }

    public String getUsername(String token) {
        return parse(token).getPayload().getSubject();
    }

    public List<String> getRoles(String token) {
        var claims = parse(token).getPayload();
        List<?> raw = claims.get("roles", List.class);
        return raw == null ? List.of() : raw.stream().map(String::valueOf).toList();
    }
}
