package main;

import gui.ClienteFrame;
import gui.InicioDialogo;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {

    static Socket conexion;
    static ClienteFrame frame;
    static String servidor = "localhost";
    public static int defaultPort = 4444;
    static int puerto = defaultPort;

    static DataOutputStream sender;
    static String nombreUsuario = "";

    public static void main(String[] args) {
        //pedirNombre();
        configurarGUI();
        lanzarDialogoInicio();
        if (establecerConexion()) {
            if (inicializarSender()) {
                escuchar();
            }
        }
    }

    static void configurarGUI(){

    }

    public static boolean establecerConexion() {
        boolean correcto = false;
        try {
            System.out.println("Intentando conexión con el servidor " + servidor + " en el puerto " + puerto);
            conexion = new Socket(servidor, puerto);
            correcto = true;
            System.out.println("Conexión con el servidor establecida correctamente");
        } catch (IOException e) {
            System.err.println("Error al intentar conectar con el servidor");
            e.printStackTrace();
            mostrarError("No se ha podido establecer la conexión con el servidor.", "Conexión fallida");
        }
        return correcto;
    }

    public static boolean inicializarSender() {
        boolean correcto = false;
        try {
            sender = new DataOutputStream(conexion.getOutputStream());
            correcto = true;
        } catch (IOException e) {
            mostrarError("No se ha podido inicializar el sender", "Error al crear sender");
            throw new RuntimeException(e);
        }
        return correcto;
    }

    public static void iniciarClienteFrame() {
        frame = new ClienteFrame(nombreUsuario);
        frame.setVisible(true);
        System.out.println("GUI iniciada");
    }

    public static void enviar(String mensaje) {
        try {
            sender.writeUTF(mensaje);
            System.out.println("Se ha enviado: " + mensaje);
        } catch (IOException e) {
            mostrarError("No se ha podido enviar el mensaje", "Error");
            e.printStackTrace();
        }
    }

    public static void escuchar() {
        try {
            DataInputStream dis = new DataInputStream(conexion.getInputStream());
            while (true) {
                System.out.println("Esperando a recibir una cadena...");
                String recibido = dis.readUTF().trim();
                System.out.println("Cadena recibida: \"" + recibido + "\"");
                tratarCadenaRecibida(recibido);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void tratarCadenaRecibida(String recibido) {
        String contenido = recibido.substring(2);
        if (recibido.startsWith("J:")) { // J = joined (unido a la sala)
            tratarUnido(contenido);
        } else if (recibido.startsWith("N:")) { // N = notificación
            tratarNotificacion(contenido);
        } else if (recibido.startsWith("R:")) { // R = request (petición)
            tratarRequest(contenido);
        } else if (recibido.startsWith("M:")) { // M = mensaje
            tratarMensaje(contenido);
        } else if (recibido.startsWith("C:")) { // C = cierre
            tratarCierre(contenido);
        } else {
            System.err.println("La cadena recibida tiene un formato incorrecto");
        }
    }

    private static void tratarUnido(String historialChat) {
        iniciarClienteFrame();
        if (!historialChat.trim().isEmpty()) {
            System.out.println("Cargando historial de mensajes");
            String[] mensajes = historialChat.split("\n");
            for (String m : mensajes) {
                if (!m.isEmpty()) {
                    tratarCadenaRecibida(m);
                }
            }
        } else {
            System.out.println("Historial de mensajes vacío");
        }
    }

    private static void tratarNotificacion(String notificacion) {
        frame.anadirTexto(notificacion);
    }

    private static void tratarRequest(String peticion) {
        if (peticion.startsWith("username")) {
            System.out.println("Petición de nombre de usuario");
            //pedirNombre();
            enviar("U:" + nombreUsuario);
        } else {
            System.err.println("Petición desconocida");
        }
    }

    private static void tratarMensaje(String mensaje) {
        int posSeparador = mensaje.indexOf(">");
        if (posSeparador != -1) {
            // Separo el nombre del usuario del contenido del mensaje
            String emisor = mensaje.substring(0, posSeparador);
            String contenidoMensaje = mensaje.substring(posSeparador + 1);
            System.out.println("El usuario \"" + emisor + " ha enviado el mensaje \"" + contenidoMensaje + "\"");
            // Añado el mensaje al GUI
            frame.anadirMensaje(emisor, contenidoMensaje);
        } else {
            System.err.println("Mensaje con formato incorrecto: \"" + mensaje + "\"");
        }
    }

    private static void tratarCierre(String mensajeCierre) {
        System.out.println("Recibida notificación de cierre de la conexión con el siguiente mensaje: \""
                + mensajeCierre + "\"");
        JOptionPane.showMessageDialog(null, mensajeCierre);
        System.out.println("Cerrando la aplicación");
        System.exit(0);
    }

    static void pedirNombre() {
        boolean nombreCorrecto = false;
        while (!nombreCorrecto) {
            System.out.println("Pidiendo al usuario que elija un nombre");
            //nombreUsuario = JOptionPane.showInputDialog("Introduce tu nombre: ");
            lanzarDialogoInicio();
            if (nombreUsuario.trim().equals("")) {
                System.err.println("El usuario ha introducido un nombre no válido");
                mostrarError("Elige un nombre válido", "Error");
            } else {
                nombreCorrecto = true;
                System.out.println("El usuario ha elegido el nombre: " + nombreUsuario);
            }
        }
    }

    static void lanzarDialogoInicio() {
        System.out.println("Lanzando diálogo de inicio");
        InicioDialogo dialogo = new gui.InicioDialogo();
        dialogo.pack();
        dialogo.inicializarCampos(defaultPort);
        dialogo.setLocationRelativeTo(null);
        dialogo.setVisible(true);
    }

    public static void setServidorYUsuario(String direccionServidor, int puertoServidor, String nombreUsuario) {
        servidor = direccionServidor;
        puerto = puertoServidor;
        Main.nombreUsuario = nombreUsuario;
    }

    public static void mostrarError(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

}