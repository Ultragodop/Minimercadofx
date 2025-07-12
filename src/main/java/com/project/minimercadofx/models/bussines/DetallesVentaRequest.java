package com.project.minimercadofx.models.bussines;

public class DetallesVentaRequest {

        private Integer idProducto;
        private Integer cantidad;

        public DetallesVentaRequest(){
        }

        public Integer getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Integer idProducto) {
            this.idProducto = idProducto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

}
