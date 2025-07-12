package com.project.minimercadofx.models.bussines;

public class DetallesVenta {

    Integer idProducto;
    Integer cantidad;
    String nombreProducto;
    Double PrecioUnitario;

    public DetallesVenta() {
    }

    public Integer getId() {
        return idProducto;
    }
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setId(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Double getPrecio() {
        return PrecioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        PrecioUnitario = precioUnitario;
    }
}
