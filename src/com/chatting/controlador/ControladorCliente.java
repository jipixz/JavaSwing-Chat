package com.chatting.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import com.chatting.Constantes;
import com.chatting.modelo.Mensaje;
import com.chatting.modelo.UtilidadesCliente;
import com.chatting.vista.VistaCliente;

/**
 * Clase que tratará los eventos sobre los botones en la vista.
 *
 */
public class ControladorCliente implements ActionListener {

	private UtilidadesCliente cliente;
	private final VistaCliente vista;

	public ControladorCliente(final VistaCliente vista) {
		this.vista = vista;
	}

	public void setCliente(final UtilidadesCliente cliente) {
		this.cliente = cliente;
	}

	/**
	 * Interpreta las acciones realizadas sobre el cliente.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		switch (e.getActionCommand()) {
		case "salir":
			try {
				salir();
			} catch (ClassNotFoundException | IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case "enviar":
			final Mensaje msg = new Mensaje(cliente.getNickname(), vista.getTextoCampo());
			try {
				cliente.enviarTCP(msg);
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			final Boolean temp = vista.isPrivateChat; // initial state for private
			vista.isPrivateChat = false; // set off to get normal message
			if (temp) {
				vista.appendChat(vista.getTextoCampo());
			}
			vista.isPrivateChat = temp; // set the current state again
			vista.vaciarTextoCampo();
			break;
		case "listado":
			final Mensaje listarMsg = new Mensaje(cliente.getNickname(), Constantes.CODIGO_LISTAR);
			try {
				cliente.enviarTCP(listarMsg);
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case "limpiar":
			vista.limpiarChat();
			break;
		case "scroll":
			vista.alternarAutoScroll();
			break;
		default:
			break;
		}
	}

	/**
	 * Método que desconecta y apaga el cliente.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public int salir() throws ClassNotFoundException, IOException {
		final Mensaje OutputMessage = new Mensaje(cliente.getNickname(), Constantes.CODIGO_SALIDA);
		cliente.enviarTCP(OutputMessage);
		cliente.cerrarConexion();
		vista.setClientes("Unknown");
		vista.addText("<CLIENT> Has abandonado la sala de chat.");
		vista.setEnabled(false);
		return 0;
	}
	


}
