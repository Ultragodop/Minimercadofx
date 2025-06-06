package com.project.minimercadofx.services.http;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
public class HttpClientHelper {

    /**
     * Envía una petición HTTP (GET, POST, PUT, DELETE, etc.) al endpoint indicado.
     * Si existe un token JWT en Session, lo añade automáticamente en el header Authorization.
     *
     * @param endpoint La URL completa del endpoint (p. ej. "http://192.168.0.45/api/inventario")
     * @param method   El método HTTP ("GET", "POST", "PUT", "DELETE", etc.)
     * @param jsonBody El cuerpo JSON (solo para POST/PUT; para GET o DELETE pásalo null)
     * @return El cuerpo de la respuesta como String
     * @throws IOException Si hay error de conexión o lectura
     */
    public String sendRequest(String endpoint, String method, String jsonBody) throws IOException {
        // 1) Construir la URL y abrir la conexión
        long startTime = System.currentTimeMillis();
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 2) Configurar método y headers básicos
        connection.setRequestMethod(method.toUpperCase());
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        // 3) Leer el token de Session (si existe) y agregar al header Authorization
        String jwt = Session.getToken();
        if (jwt != null && !jwt.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + jwt);
            System.out.println("[HttpClientHelper] Header Authorization agregado: Bearer " + jwt);
        } else {
            System.out.println("[HttpClientHelper] No hay token en Session; no se agrega header Authorization.");
        }

        // 4) Si es POST o PUT y hay un JSON en jsonBody, escribirlo en el OutputStream
        if ((method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) && jsonBody != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        // 5) Forzar la conexión y obtener código de respuesta
        int responseCode = connection.getResponseCode();
        System.out.println("[HttpClientHelper] URL: " + endpoint + "  Método: " + method + "  Código HTTP: " + responseCode);

        // 6) Seleccionar InputStream o ErrorStream según responseCode
        InputStream is = (responseCode >= 200 && responseCode < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();

        if (is == null) {
            // Si ni el InputStream ni el ErrorStream existen, lanzamos excepción
            throw new IOException("Error HTTP " + responseCode + ": sin cuerpo en la respuesta.");
        }

        // 7) Leer el cuerpo completo de la respuesta
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("[HttpClientHelper] Respuesta recibida en " + (endTime - startTime) + " ms");
            return sb.toString();

        }
    }
}
