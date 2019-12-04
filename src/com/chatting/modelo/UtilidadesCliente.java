package com.chatting.modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.chatting.Constantes;
import com.chatting.controlador.ControladorCliente;
import com.chatting.vista.VistaCliente;

/**
 * Clase que provee de las herramientas para manipular datos con el cliente.
 *
 */
public class UtilidadesCliente {

	private VistaCliente vista;
	private ControladorCliente controlador;
	private Socket cliente;
	private String Nickname;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	
	/* ======================== Métodos ========================== */
	
	public UtilidadesCliente(Socket cliente, VistaCliente vista, ControladorCliente controlador, String nickname) throws IOException {
		
		System.out.println("start constructor");
		this.cliente = cliente;
		this.vista = vista;
		this.controlador = controlador;
		System.out.println("middle constructor");

		this.Nickname = nickname;
		System.out.println("middle2 constructor");

		this.salida = new ObjectOutputStream(cliente.getOutputStream());
		System.out.println("end constructor");
		this.entrada = new ObjectInputStream(cliente.getInputStream());
		System.out.println("end2 constructor");
		
	}
	
	/* ======================== Métodos ========================== */
	
	/**
	 * Espera hasta recibir una cadena.
	 * @return
	 * @throws IOException 
	 */
	public String getNickname(){
		return	this.Nickname;
	}
	public Mensaje recibirTCP() throws IOException, ClassNotFoundException {
		Mensaje cadenaRecibida = null;
		do {
				cadenaRecibida = (Mensaje) entrada.readObject();
		} while(cadenaRecibida==null);
			
		return cadenaRecibida;
	}
	
	/**
	 * Envía un dato.
	 * @param cadena
	 */
	public void enviarTCP(Mensaje cadena) throws IOException, ClassNotFoundException{
			salida.writeObject(cadena);
	}
	
	/**
	 * Cerramos la conexión del socket.
	 */
	public void cerrarConexion() {
		try {
			entrada.close();
			salida.close();
			cliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Interpretamos el mensaje leido en el cliente.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void handleMessage() throws ClassNotFoundException, IOException {
		try {
			Mensaje msg = recibirTCP();
			switch(msg.getMessage().trim()){
				// recibimos código de desconectar.
				case Constantes.CODIGO_SALIDA:
					controlador.salir();
					vista.addText("<CLIENT> El servidor se ha apagado");
				break;
				// Recibimos actualizar numero clientes
				case Constantes.CODIGO_ACTUALIZAR_CONECTADOS:
					Mensaje newmsg = recibirTCP();
					vista.setClientes(newmsg.getMessage());
				break;
				default: // Recibimos un mensaje normal y corriente
					vista.addText(msg.getMessage());
				break;
			}
	    	
		} catch (IOException e) {
			controlador.salir();
			vista.addText("<CLIENT> Servidor desconectado.");
		}
    }
}
