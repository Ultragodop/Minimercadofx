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

    private String categoriaNombre;
    private String proveedorNombre;
    private Boolean activo = true;
@Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precioCompra=" + precioCompra +
                ", precioVenta=" + precioVenta +
                ", fechaVencimiento=" + fechaVencimiento +
                ", idCategoria=" + idCategoria +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                ", idProveedor=" + idProveedor +
                ", categoriaNombre='" + categoriaNombre + '\'' +
                ", proveedorNombre='" + proveedorNombre + '\'' +
                ", activo=" + activo +
                '}';
    }


}
