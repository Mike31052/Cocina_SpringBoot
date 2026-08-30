package com.negocio.pedidos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey llave;
    private final long expiracionMs;

    public JwtService(
        @Value("${jwt.secreto}") String secreto,
        @Value("${jwt.expiracion-horas:12}") long expiracionHoras
    ) {
        this.llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionHoras * 60 * 60 * 1000;
    }

    public String generarToken(String username, String rol) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
            .subject(username)
            .claim("rol", rol)
            .issuedAt(ahora)
            .expiration(expira)
            .signWith(llave)
            .compact();
    }

    public String extraerUsername(String token) {
        return parsearClaims(token).getSubject();
    }

    public boolean esValido(String token) {
        try {
            Claims claims = parsearClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parsearClaims(String token) {
        return Jwts.parser()
            .verifyWith(llave)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
