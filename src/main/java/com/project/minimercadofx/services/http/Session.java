package com.project.minimercadofx.services.http;




public class Session {

    public static String token;

    public static void setToken(String token) {
        Session.token = token;
    }
    public static String getToken() {
        return token;
    }
}
