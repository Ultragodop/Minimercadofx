package com.project.minimercadofx.models.bussines;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter


public class Producto {
    private Integer id;
    private String nombre;
    private String descripcion;


    private Double precioCompra;


    private Double precioVenta;


    private Date fechaVencimiento;

    private Integer idCategoria;


    private Integer stockActual;


    private Integer stockMinimo;


    private Integer idProveedor;


    private Boolean activo = true;

    public Producto(Integer id) {
        this.id = id;
    }
}
