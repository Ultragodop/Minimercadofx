package com.project.minimercadofx.models.bussines;

import java.util.List;

public class VentaRequest {
    private long idUsuario;
    private List<DetallesVentaRequest> detallesVenta;

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<DetallesVentaRequest> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetalleVentas(List<DetallesVentaRequest> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }
}
