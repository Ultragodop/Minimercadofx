package com.project.minimercadofx.models.bussines;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class CategoriaDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private Date fechaCreacion;
    private Object[] productos;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public boolean isActivo() { return activo; }
    
    @JsonProperty("activo")
    public void setActivo(Object activo) {
        if (activo instanceof Boolean) {
            this.activo = (Boolean) activo;
        } else if (activo instanceof String) {
            this.activo = Boolean.parseBoolean((String) activo);
        }
    }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Object[] getProductos() { return productos; }
    public void setProductos(Object[] productos) { this.productos = productos; }
}
