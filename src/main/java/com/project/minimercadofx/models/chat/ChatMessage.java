package com.project.minimercadofx.models.chat;

import lombok.Getter;
import lombok.Setter;

@Setter
public class ChatMessage {
        private String usuario;
        private String mensaje;
        private String sala;
        public ChatMessage() {}  // Para Jackson si usás JSON
        public ChatMessage(String usuario, String mensaje, String sala) {
            this.usuario = usuario;
            this.mensaje = mensaje;
            this.sala = sala;
        }
        public String getSala(){return sala;}
        public String getUsuario() {
            return usuario;
        }
        public String getMensaje() {
            return mensaje;
        }
    }


