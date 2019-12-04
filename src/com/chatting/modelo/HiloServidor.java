package com.chatting.modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.chatting.Constantes;
import com.chatting.ejecutable.Servidor;
import com.chatting.vista.VistaServidor;

/**
 * Hilo del servidor que tratará al cliente.
 *
 */
public class HiloServidor extends Thread {

	private final Socket cliente;
	private final VistaServidor vista;

	private final ObjectInputStream entrada;
	private final ObjectOutputStream salida;

	private String nombre;

	/*
	 * ======================== Constructor y ejecución ==========================
	 */

	public HiloServidor(final VistaServidor vista, final Socket cliente) throws IOException {
		this.vista = vista;
		this.cliente = cliente;
		this.cliente.setSoTimeout(5000);
		nombre = "";
		entrada = new ObjectInputStream((cliente.getInputStream()));
		salida = new ObjectOutputStream((cliente.getOutputStream()));
	}

	public void run() {
		Mensaje mensaje;
		try {
			inicializacionCliente();
		} catch (final ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			do {
				mensaje = (Mensaje) recibirTCP();
				messageHandler(mensaje);
			}while(!mensaje.getMessage().trim().equals(Constantes.CODIGO_SALIDA));
			
			entrada.close();
			salida.close();
			cliente.close();
		} catch(final SocketTimeoutException e) { 
			final Mensaje server = new Mensaje("Server","<SERVER> "+nombre+" se ha caído (connection timeout).");
			try {
				Servidor.imprimirTodos(server);
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Servidor.sacarCliente(nombre);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch(final IOException e) { 
			final Mensaje server = new Mensaje("Server","<SERVER> "+nombre+" desconectado dolorosamente.");

			try {
				Servidor.imprimirTodos(server);
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (final ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		vista.setClientesConectados(Servidor.getClientes().getClientesConectados());
	}
	
	/* ======================== Métodos ========================== */
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(final String nombre) {
		this.nombre = nombre;
	}
	
	public void cerrarConexion() throws IOException {
		final Mensaje outCode = new Mensaje(getNombre(),Constantes.CODIGO_SALIDA);
		enviarTCP(outCode);
	}
	
	/**
	 * Aquí tratamos los mensajes que le lleguen al server.
	 * 
	 * @param mensaje
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void messageHandler(final Mensaje msg) throws ClassNotFoundException, IOException {
		final String mensaje = msg.getMessage();
		switch(mensaje.trim()) {
			case Constantes.CODIGO_NICK:
				
				cambioNick();
				
			break;
			case Constantes.CODIGO_SALIDA:
				final Mensaje server = new Mensaje("Server","<SERVER> "+ nombre+ " ha abandonado el chat.");
				Servidor.imprimirTodos(server);
				Servidor.sacarCliente(nombre);
				
			break;
			case Constantes.CODIGO_LISTAR:
				final Mensaje listing = new Mensaje("Server","<SERVER> CLIENTES CONECTADOS: " + new String(Servidor.getClientes().getListaClientes()));
				enviarTCP(listing);
				
			break;
			default:
				final String[] parts= mensaje.split(" ",3);
				if (parts[0].contains("/PRIVATE")) {
					final Mensaje MessageContainer = new Mensaje(nombre, parts[1], nombre+": "+parts[2]);
					//Servidor.imprimirA(MessageContainer);
					Servidor.imprimirTodos(MessageContainer);

				} else{
					final Mensaje MessageContainer = new Mensaje(nombre, nombre+": "+mensaje);
					Servidor.imprimirTodos(MessageContainer);
				}
			
			break;
		}
	}

	/**
	 * Lo que hacemos cuando un cliente envía el código de cambio de nick.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void cambioNick() throws ClassNotFoundException, IOException {
		final String nombreAnterior = nombre;
		Servidor.sacarCliente(nombreAnterior);
		final Mensaje msg = recibirTCP();
		nombre = nombreNoRepetido(msg.getMessage());
		Servidor.meterCliente(this);
		final Mensaje Server = new Mensaje("Server","<SERVER> "+ nombreAnterior + " ha cambiado su nombre por "+ nombre +".");
		
		Servidor.imprimirTodos(Server);
	}

	/**
	 * Aquí inicializamos el cliente (darle nombre y meterlo en listaClientes)
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void inicializacionCliente() throws ClassNotFoundException, IOException {
		final Mensaje msg = recibirTCP();

    nombre = nombreNoRepetido(msg.getMessage());
		
		Servidor.meterCliente(this);
		Servidor.getClientes().actualizarConectados();
		final Mensaje Joinchat = new Mensaje("Server","<SERVER> "+ nombre + " se ha unido al chat.");
		final Mensaje connectedClient = new Mensaje("Server","<SERVER> CLIENTES CONECTADOS: " + new String(Servidor.getClientes().getListaClientes()));

		Servidor.imprimirTodos(Joinchat);
		Servidor.imprimirTodos(connectedClient);

	}
	
	/**
	 * Modifica el nombre que recibe para que no esté repetido en la lista de clientes.
	 * @param nombreViejo
	 * @return
	 */
	private String nombreNoRepetido(final String nombreViejo) {
		// Si ya existe un cliente que se llame así, lo renombramos
    	String nuevoNombre = nombreViejo; int i = 1;
    	while(Servidor.getClientes().yaEstaDentro(nuevoNombre)) { 
    		nuevoNombre = nombreViejo.concat(Integer.toString(i));
    		i++; 
    	}
    	return nuevoNombre;
	}
	
	/**
	 * Recibe un dato.
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Mensaje recibirTCP() throws ClassNotFoundException {
		Mensaje cadenaRecibida = null;
		do {
			try {
				cadenaRecibida = (Mensaje) entrada.readObject();
			} catch (final IOException e) { cadenaRecibida = null; }
		} while(cadenaRecibida == null);
			
		return cadenaRecibida;
	}
	
	/**
	 * Envía un dato.
	 * 
	 * @param cadena
	 * @throws IOException
	 */
	public void enviarTCP(final Mensaje cadena) throws IOException {
			salida.writeObject(cadena );
	}
	
}
