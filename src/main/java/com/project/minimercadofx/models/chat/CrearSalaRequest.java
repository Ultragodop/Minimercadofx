package com.project.minimercadofx.models.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CrearSalaRequest {
    private String nombre;
    private Long creadorId;
    private List<Long> usuariosAutorizadosIds;

    // Getters y setters
}
