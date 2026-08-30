package com.negocio.pedidos.controller;

import com.negocio.pedidos.dto.LoginRequest;
import com.negocio.pedidos.dto.LoginResponse;
import com.negocio.pedidos.security.JwtService;
import com.negocio.pedidos.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
            var usuario = principal.getUsuario();

            String token = jwtService.generarToken(usuario.getUsername(), usuario.getRol().name());

            return new LoginResponse(token, usuario.getUsername(), usuario.getNombre(), usuario.getRol());
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }
    }
}
