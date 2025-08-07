package com.project.minimercadofx.models.bussines;

import java.math.BigDecimal;
import java.time.Instant;

public class VentaDTO {
    Integer idVenta;
    String nombre;
    Instant fecha;
    String tipoPago;
    String estado;
    BigDecimal total;

    public VentaDTO() {
    }
    public VentaDTO(Integer idVenta, String nombre, Instant fecha, String tipoPago, String estado, BigDecimal total) {
        this.idVenta = idVenta;
        this.nombre = nombre;
        this.fecha = fecha;
        this.tipoPago = tipoPago;
        this.estado = estado;
        this.total = total;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
