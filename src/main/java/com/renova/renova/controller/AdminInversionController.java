package com.renova.renova.controller;

import com.renova.renova.dto.InversionRequest;
import com.renova.renova.dto.InversionResponse;
import com.renova.renova.dto.InversionSummary;
import com.renova.renova.service.InversionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CRUD de inversiones para el panel de administracion. Requiere token Bearer (ver AdminAuthInterceptor). */
@RestController
@RequestMapping("/api/admin/inversiones")
public class AdminInversionController {

    private final InversionService inversionService;

    public AdminInversionController(InversionService inversionService) {
        this.inversionService = inversionService;
    }

    @GetMapping
    public List<InversionSummary> listar() {
        return inversionService.listarTodas();
    }

    @GetMapping("/{id}")
    public InversionResponse obtener(@PathVariable Long id) {
        return inversionService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InversionResponse crear(@Valid @RequestBody InversionRequest request) {
        return inversionService.crear(request);
    }

    @PutMapping("/{id}")
    public InversionResponse actualizar(@PathVariable Long id, @Valid @RequestBody InversionRequest request) {
        return inversionService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        inversionService.eliminar(id);
    }
}
