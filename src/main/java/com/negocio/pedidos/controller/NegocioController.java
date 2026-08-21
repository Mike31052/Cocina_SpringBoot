package com.negocio.pedidos.controller;

import com.negocio.pedidos.model.Negocio;
import com.negocio.pedidos.repository.NegocioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/negocios")
@RequiredArgsConstructor
public class NegocioController {

    private final NegocioRepository negocioRepository;

    @GetMapping
    public List<Negocio> listar(@RequestParam(required = false) String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return negocioRepository.findAll();
        }
        return negocioRepository.findByNombreContainingIgnoreCase(buscar);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Negocio crear(@Valid @RequestBody NuevoNegocioRequest request) {
        Negocio negocio = Negocio.builder()
            .nombre(request.nombre())
            .direccion(request.direccion())
            .telefono(request.telefono())
            .contacto(request.contacto())
            .build();
        return negocioRepository.save(negocio);
    }

    public record NuevoNegocioRequest(
        @NotBlank String nombre,
        String direccion,
        String telefono,
        String contacto
    ) {}
}
