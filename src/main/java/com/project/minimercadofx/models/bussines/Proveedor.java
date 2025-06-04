package com.project.minimercadofx.models.bussines;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Proveedor {
    private Integer id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String email;
    public boolean activo= true;

    public Proveedor() {
    }

    public Proveedor(String nombre, String telefono, String direccion, String email, boolean activo) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.activo = true;

    }


}
