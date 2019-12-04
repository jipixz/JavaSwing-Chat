package com.chatting.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import com.chatting.controlador.ControladorCliente;

/**
 * Ventana del cliente
 *
 */
public class VistaCliente extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JFrame ventana;
	WindowListener exitListener;
	
	private JLabel labelClientes;
	private JTextArea chat;
	private JList<String> listaUsuarios;
	private JTextField campo;
	private JButton botonEnviar, botonSalir, botonLimpiar, botonListado, botonScroll;
	DefaultCaret caret;
	
	/* ============================| Constructores |============================ */

	public VistaCliente(JFrame ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		JPanel panelNorte = new JPanel(new FlowLayout());
		JPanel panelSur = new JPanel(new GridLayout(1,3));
		JPanel panelCentro = new JPanel(new GridLayout(1,2));
		//JPanel panelLeft = new JPanel(new FlowLayout());
		//JPanel panelNorte = new JPanel(new FlowLayout());
		/* --------------------- Inicializaciones --------------------- */
		labelClientes = new JLabel("Clientes en el chat: 0/0");
		listaUsuarios = new JList<String>();
		chat = new JTextArea();
		campo = new JTextField();
		botonListado = new JButton("Listado de clientes");
		botonSalir = new JButton("Salir");
		botonEnviar = new JButton("Enviar");
		botonLimpiar = new JButton("Limpiar chat");
		botonScroll = new JButton("Auto-scroll");
		JScrollPane scroll = new JScrollPane(chat);
		JScrollPane scrollUser = new JScrollPane(listaUsuarios);
		/* --------------------- Asignaciones --------------------- */
		panelNorte.add(labelClientes);
		panelNorte.add(botonListado);
		panelNorte.add(botonScroll);
		panelNorte.add(botonSalir);

		panelSur.add(botonLimpiar);
		panelSur.add(campo);
		
		panelSur.add(botonEnviar);
		panelCentro.add(scrollUser);
		panelCentro.add(scroll);
		add(panelNorte, BorderLayout.NORTH);
		add(panelSur, BorderLayout.SOUTH);
		add(panelCentro,BorderLayout.CENTER);
		
		
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollUser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		caret = (DefaultCaret)chat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		setPreferredSize(new Dimension(480, 360));
		chat.setLineWrap(true);
		
		chat.setEditable(false);
		setEnabled(false);
	}
	
	/* ============================| Métodos |============================ */
	
	public String getTextoCampo() {
		return campo.getText().toString();
	}
	
	public void vaciarTextoCampo() {
		campo.setText("");
	}
	
	public void setClientes(String clientes) {
		labelClientes.setText("Clientes en el chat: "+ clientes);
	}
	
	public void addText(String linea) {
		StringBuffer sb = new StringBuffer("<SERVER> CLIENTES CONECTADOS:");
		if (linea.contains(sb)) {
			DefaultListModel<String> model = new DefaultListModel<String>();
			// Usuarios.setText("");
			String[] users = linea.substring(29).split("[\\,\\.]");
			for(int i=0; i < users.length;i++) {
				// Usuarios.append(users[i]+"\n");				
				model.addElement(users[i]);
			}
			this.listaUsuarios.setModel(model);
		} else {
			chat.append(linea+"\n");			
		}
	}
	
	public void limpiarChat() {
		chat.setText("");
	}
	
	public void alternarAutoScroll() {
		if(caret.getUpdatePolicy() != DefaultCaret.NEVER_UPDATE)
			caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		else {
			chat.setCaretPosition(chat.getDocument().getLength() );
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		}
	}
	
	public void setEnabled(boolean activado) {
		campo.setEnabled(activado);
		chat.setEnabled(activado);
		botonEnviar.setEnabled(activado);
		botonLimpiar.setEnabled(activado);
		botonListado.setEnabled(activado);
		botonSalir.setEnabled(activado);
		botonScroll.setEnabled(activado);
	}
	
	public void setControlador(ControladorCliente l) {
		botonEnviar.setActionCommand("enviar");
		campo.setActionCommand("enviar");
		botonSalir.setActionCommand("salir");
		botonLimpiar.setActionCommand("limpiar");
		botonListado.setActionCommand("listado");
		botonScroll.setActionCommand("scroll");

		botonEnviar.addActionListener(l);
		campo.addActionListener(l);
		botonSalir.addActionListener(l);
		botonLimpiar.addActionListener(l);
		botonListado.addActionListener(l);
		botonScroll.addActionListener(l);
		
		// Controlador de cierre de ventana (para que se desconecte bien al cerrar)
		exitListener = new WindowAdapter() {

		    @Override
		    public void windowClosing(WindowEvent e) {
		        l.salir();
		        System.exit(0);
		    }
		};
		ventana.addWindowListener(exitListener);
		
	}	
	
}
