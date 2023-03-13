package main;

import data.Usuario;
import data.SalaChat;
import hilos.HiloConexion;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    static ServerSocket servidor;
    static final int PUERTO = 4444;
    static SalaChat sala;
    static int MAXIMO_CONEXIONES = 10;
    static ArrayList<Thread> hilos = new ArrayList<>();

    public static void main(String[] args) {
        if (iniciarServidor()) {
            sala = new SalaChat(MAXIMO_CONEXIONES);
            System.out.println("Se ha creado la sala con un límite máximo establecido en " + MAXIMO_CONEXIONES + " conexiones");
            while (sala.getNumUsuarios() < MAXIMO_CONEXIONES) {
                System.out.println("Conexiones actuales: " + sala.getNumUsuarios());
                System.out.println("Esperando conexiones...");
                Socket nuevaConexion = aceptarConexion();
                if (nuevaConexion != null) {
                    Usuario usuario = new Usuario(nuevaConexion);
                    if (usuario.pedirNombreUsuario()) {
                        if(sala.nombreDisponible(usuario.getNombreUsuario())){
                            sala.anadirUsuario(usuario);
                            HiloConexion hilo = new HiloConexion(sala, usuario);
                            Thread t = new Thread(hilo);
                            t.start();
                            hilos.add(t);
                        }else{
                            System.out.printf("El nombre ya está cogido por otro usuario");
                            usuario.enviar("C:Ese nombre no está disponible");
                        }
                    }else{
                        System.out.println("No se puede unir a la sala sin un nombre válido. Cerrando la conexión");
                        usuario.enviar("C:El nombre no puede estar vacío");
                    }
                }
            }
            System.out.println("Se ha llegado al límite de conexiones (" + MAXIMO_CONEXIONES + ")");
            esperarHilos();
            cerrarServidor();
        }
    }

    private static boolean iniciarServidor() {
        boolean correcto = false;
        try {
            servidor = new ServerSocket(PUERTO);
            correcto = true;
            System.out.println("Servidor arrancado en el puerto " + PUERTO);
        } catch (IOException e) {
            System.err.println("Error al intentar arrancar el sevidor");
            throw new RuntimeException(e);
        }
        return correcto;
    }

    private static Socket aceptarConexion() {
        Socket conexion = null;
        try {
            conexion = servidor.accept();
            System.out.println("Aceptada conexion con " + conexion.getInetAddress().getHostAddress());
        } catch (IOException e) {
            System.err.println("Error al intentar aceptar conexión de " + conexion.getInetAddress().getHostAddress());
            throw new RuntimeException(e);
        }
        return conexion;
    }

    private static void esperarHilos(){
        try {
            System.out.println("El servidor se mantendrá abierto hasta que se cierren todos los hilos actuales");
            for (Thread t : hilos) {
                t.join();
            }
        } catch (InterruptedException e) {
            sala.broadcast("C:");
            throw new RuntimeException(e);
        }
    }

    private static void cerrarServidor() {
        try {
            sala.broadcast("C:");
            servidor.close();
            System.out.println("Servidor desconectado");
        } catch (IOException e) {
            System.err.println("Error al intentar apagar el servidor");
            throw new RuntimeException(e);
        }
    }

}