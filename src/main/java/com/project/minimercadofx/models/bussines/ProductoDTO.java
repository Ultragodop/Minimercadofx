package com.project.minimercadofx.models.bussines;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class ProductoDTO {
    private Long id;
    private Long fechaVencimiento; // Lo convertimos luego a LocalDate si querés
    private boolean activo;
    private String nombre;
    private int stockMinimo;
    private String descripcion;
    private double precioVenta;
    private int stockActual;
    private double precioCompra;
    private String categoriaNombre;
    private String proveedorNombre;

}


