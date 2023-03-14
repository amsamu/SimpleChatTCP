package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class ClienteFrame extends JFrame implements ActionListener, KeyListener {

    String nombre;
    JTextField campoMensaje = new JTextField();
    JScrollPane scrollpanel;
    JTextArea textarea;
    JButton botonEnviar = new JButton("Enviar");
    JButton botonSalir = new JButton("Salir");

    public ClienteFrame(String nombre) {
        super("Conexión con chat: " + nombre);
        this.nombre = nombre;
        setSize(530, 420);
        setLayout(null);
        campoMensaje.setBounds(10, 10, 400, 30);
        this.add(campoMensaje);
        textarea = new JTextArea();
        scrollpanel = new JScrollPane(textarea);
        scrollpanel.setBounds(10, 50, 400, 300);
        this.add(scrollpanel);
        botonEnviar.setBounds(420, 10, 100, 30);
        botonEnviar.addActionListener(this);
        this.add(botonEnviar);
        botonSalir.setBounds(420, 50, 100, 30);
        botonSalir.addActionListener(this);
        this.add(botonSalir);
        textarea.setBounds(0, 0, 400, 300);
        textarea.setEditable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// call onCancel() on ESCAPE
        campoMensaje.addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonEnviar) {
            String texto = campoMensaje.getText();
            main.Main.enviar("M:" + nombre + ">" + texto);
            campoMensaje.setText("");
        }
        if (e.getSource() == botonSalir) {
            System.out.println("El usuario ha pulsado \"Salir\"");
            main.Main.enviar("E:");
            System.out.println("Cerrando la aplicación");
            System.exit(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            botonEnviar.doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void anadirTexto(String texto) {
        textarea.setText(textarea.getText() + texto + "\n");
    }

    public void anadirMensaje(String emisor, String mensaje) {
        anadirTexto(formatearMensaje(emisor, mensaje));
    }

    private String formatearMensaje(String emisor, String mensaje) {
        return emisor + "> " + mensaje;
    }

}
