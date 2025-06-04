package com.project.minimercadofx.models.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class User {
    private static String nombre;
    public static void setNombre(String nombre) {
        User.nombre = nombre;
    }
    public static String getNombre() {
        return nombre;
    }
}
