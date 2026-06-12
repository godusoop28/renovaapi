package com.renova.renova.controller;

import com.renova.renova.dto.InversionResponse;
import com.renova.renova.service.InversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inversiones")
public class InversionPublicController {

    private final InversionService inversionService;

    public InversionPublicController(InversionService inversionService) {
        this.inversionService = inversionService;
    }

    /** Consulta publica del estado de una inversion mediante el codigo (NIP) de seguimiento. */
    @GetMapping("/{codigo}")
    public InversionResponse buscarPorCodigo(@PathVariable String codigo) {
        return inversionService.buscarPorCodigo(codigo);
    }
}
