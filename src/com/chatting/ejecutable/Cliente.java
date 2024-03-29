package com.chatting.ejecutable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.chatting.controlador.ControladorCliente;
import com.chatting.modelo.Mensaje;
import com.chatting.modelo.UtilidadesCliente;
import com.chatting.vista.VistaCliente;

/**
 *  Clase principal del usuario cliente de chat.
 *
 */
public class Cliente {
	
	private static JFrame ventana;
	private static VistaCliente vista;
	private static ControladorCliente controlador;
	private static Socket cliente;
	private static UtilidadesCliente utilidades;
	/* ======================== Main ========================== */

	public static void main(String[] args) throws ClassNotFoundException {
		
		configurarVentana();
		
		try {
			
			iniciarCliente();
			
			while(!cliente.isClosed()) {
				utilidades.handleMessage();
			}
			
			while(true) {}
		} catch (SocketTimeoutException e) {
			vista.setEnabled(false);
			JOptionPane.showMessageDialog(ventana, "Conexión perdida (connection timeout)", "Error de conexión", JOptionPane.ERROR_MESSAGE);
		} catch (SocketException e) {
			vista.setEnabled(false);
			JOptionPane.showMessageDialog(ventana, "Servidor no alcanzado. Apagado o fuera de covertura.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			// El mensaje que saldrá es servidor lleno
			JOptionPane.showMessageDialog(ventana, e.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
		} 
		
	}
	
	/* ======================== Métodos ========================== */

	private static void configurarVentana() {
		/* --------------- Inicializaciones --------------- */
        ventana = new JFrame("Cliente de chat");
        vista = new VistaCliente(ventana);
        controlador = new ControladorCliente(vista);
        
        /* --------------- Configuraciones --------------- */
        ventana.setContentPane(vista);
        vista.setControlador(controlador);
        ventana.pack();
        ventana.setResizable(false);
	}
    
	/**
	 * Lanza la ventana e inicia la conexión con el servidor.
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void iniciarCliente() throws NumberFormatException, IOException, ClassNotFoundException {
    	String host = JOptionPane.showInputDialog(ventana, "Introduce la ip del host (nada = localhost)", "Datos necesarios", JOptionPane.QUESTION_MESSAGE);
    	String puerto = JOptionPane.showInputDialog(ventana, "Introduce el puerto (nada = 42455)", "Datos necesarios", JOptionPane.QUESTION_MESSAGE);
    	String nickname = JOptionPane.showInputDialog(ventana, "Introduce tu nickname", "Datos necesarios", JOptionPane.QUESTION_MESSAGE);
			
    	if(puerto.equals(""))
    		puerto = "42455";
    	if(host.equals(""))
    		host = "localhost";
		
    	try {
    		if(nickname.equals(""))
    			throw new IOException("Nickname no válido.");
					// Conectamos estableciendo un TIMEOUT
				System.out.println("ReceiverIniciarCliente");
				cliente = new Socket();
				System.out.println("socketInit");
				
				cliente.connect(new InetSocketAddress(host, Integer.parseInt(puerto)), 5000);
				System.out.println("socketConnect");
				
    		utilidades = new UtilidadesCliente(cliente, vista, controlador, nickname);
				System.out.println("sock utilities");
    		
				// Sino está lleno entramos, si está lleno lanzaremos el error.
				Mensaje msg = utilidades.recibirTCP();
    		if(msg.getMessage().trim().equals("aceptado")) {
    			iniciarChat(nickname);
    		}else {
    			utilidades = null;
    			throw new IOException("Servidor lleno");
    		}
    	}catch(NumberFormatException e) {
    		JOptionPane.showMessageDialog(ventana, "Debes introducir un número de puerto válido.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    /**
	 * Activa la ventana hacemos asociaciones correspondientes al conectar por
	 * primera vez.
	 * 
	 * @param nick
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void iniciarChat(String nick) throws IOException, ClassNotFoundException {
		System.out.println("ReceiverIniciarChat");
    ventana.setVisible(true);
		vista.setEnabled(true);
		controlador.setCliente(utilidades);
		Mensaje nickName= new Mensaje(utilidades.getNickname(),utilidades.getNickname());
		utilidades.enviarTCP(nickName);
  }
    
    
}
