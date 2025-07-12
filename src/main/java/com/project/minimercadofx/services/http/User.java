package com.project.minimercadofx.services.http;


public class User {
    private static Long id;
    private static String nombre;

    public static Long getId() {
        return id;
    }

    public static void setId(Long id) {
        User.id = id;
    }

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nombre) {
        User.nombre = nombre;
    }
}
