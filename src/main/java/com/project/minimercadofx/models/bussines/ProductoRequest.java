package com.project.minimercadofx.models.bussines;

public class ProductoRequest {
    private String nombre;
    private String descripcion;
    private Double precioCompra;
    private Double precioVenta;
    private String fechaVencimiento;
    private idCategoria idCategoria;
    private Integer stockActual;
    private Integer stockMinimo;
    private idProveedor idProveedor;
    private Boolean activo;

    public static class IdWrapper {
        private Integer id;
        public IdWrapper() {}
        public IdWrapper(Integer id) { this.id = id; }
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }
    
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    
    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    
    public idCategoria getIdCategoria() { return idCategoria; }
    public void setIdCategoria(idCategoria idCategoria) { this.idCategoria = idCategoria; }
    
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public idProveedor getIdProveedor() { return idProveedor; }
    public void setIdProveedor(idProveedor idProveedor) { this.idProveedor = idProveedor; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
