package com.project.minimercadofx.models.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CrearSalaRequest {
    private String nombre;
    private Long creadorId;
    private List<Long> usuariosAutorizadosIds;

  public String getNombre() {
      return nombre;
  }
  public void setNombre(String nombre) {
      this.nombre = nombre;
  }
  public Long getCreadorId() {
      return creadorId;
  }
  public void setCreadorId(Long creadorId) {
      this.creadorId = creadorId;
  }
  public List<Long> getUsuariosAutorizadosIds() {
      return usuariosAutorizadosIds;
  }
  public void setUsuariosAutorizadosIds(List<Long> usuariosAutorizadosIds) {
      this.usuariosAutorizadosIds = usuariosAutorizadosIds;
  }
}
