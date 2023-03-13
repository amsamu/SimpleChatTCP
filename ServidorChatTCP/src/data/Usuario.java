package data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Usuario {

    // Atributos
    private Socket conexion;
    private String nombreUsuario;
    private String host;
    private DataOutputStream sender;
    private DataInputStream receiver;

    //Constructor
    public Usuario(Socket conexion) {
        this.conexion = conexion;
        host = conexion.getInetAddress().getHostAddress();
        iniciarStreams();
    }

    private void iniciarStreams() {
        try {
            sender = new DataOutputStream(conexion.getOutputStream());
            receiver = new DataInputStream(conexion.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters y setters
    public Socket getConexion() {
        return conexion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getHost() {
        return host;
    }

    public String getNombreYHost() {
        return nombreUsuario + "@" + host;
    }


    // Métodos de socket
    public void enviar(String cadena) {
        try {
            sender.writeUTF(cadena);
            System.out.println("Se ha enviado a " + getNombreYHost() + " la siguiente cadena: \"" + cadena + "\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String recibir() {
        String recibido = null;
        try {
            System.out.println("Esperando a recibir una cadena...");
            recibido = receiver.readUTF().trim();
            System.out.println("El usuario " + getNombreYHost() + " ha enviado al servidor la siguiente cadena: \"" + recibido + "\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return recibido;
    }
    
    public boolean pedirNombreUsuario() {
        boolean correcto = true;
        // Enviar al usuario una petición de nombre de usuario
        enviar("R:username");
        // Recibir el nombre de usuario
        String recibido = recibir();
        // Tratar la cadena recibida
        if (recibido.startsWith("U:")) { // U = username
            correcto = establecerNombreUsuario(recibido.substring(2));
        }else{
            System.err.println("La cadena recibida tiene un formato incorrecto");
        }
        return correcto;
    }

    private boolean establecerNombreUsuario(String nombre) {
        boolean correcto = false;
        if (!nombre.isEmpty()) {
            setNombreUsuario(nombre);
            System.out.println("Se ha establecido el nombre de usuario " + nombre);
            correcto = true;
        } else {
            System.err.println("El nombre que se ha recibido está vacío");
        }
        return correcto;
    }

}
