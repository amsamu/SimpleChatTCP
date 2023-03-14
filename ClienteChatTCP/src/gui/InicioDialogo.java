package gui;

import main.Main;

import javax.swing.*;
import java.awt.event.*;

import static main.Main.mostrarError;

public class InicioDialogo extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField campoDireccion;
    private JTextField campoPuerto;
    private JTextField campoNombreUsuario;
    private JLabel labelServidor;
    private JLabel labelDireccion;
    private JLabel labelPuerto;
    private JSeparator separador;
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JPanel panelNombreUsuario;
    private JLabel labelNombreUsuario;

    private int puerto;

    public InicioDialogo() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setMaximumSize(getSize());
        setMinimumSize(getSize());
        setTitle("Inicio chat");

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        if (camposValidos()) {
            Main.setServidorYUsuario(campoDireccion.getText(), Integer.parseInt(campoPuerto.getText()), campoNombreUsuario.getText());
            dispose();
        }
    }

    private boolean camposValidos() {
        boolean validos = true;
        if (campoDireccion.getText().trim().isEmpty()) {
            validos = false;
            System.err.println("El usuario ha introducido una dirección no válida");
            mostrarError("Elige una dirección de servidor válida", "Error");
        } else if (campoNombreUsuario.getText().trim().isEmpty()) {
            validos = false;
            System.err.println("El usuario ha introducido un nombre no válido");
            mostrarError("Elige un nombre válido", "Error");
        }else{
            try{
                puerto = Integer.parseInt(campoPuerto.getText());
            }catch (NumberFormatException e){
                validos = false;
                mostrarError("Elige un puerto válido", "Error");
                e.printStackTrace();
            }
        }
        return validos;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void inicializarCampos(int puerto){
        campoPuerto.setText(String.valueOf(puerto));
    }

}
