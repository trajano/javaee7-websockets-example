package net.trajano.examples.websockets.web;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Echoes back the message it receives from the client.
 * 
 * @author Archimedes Trajano
 */
@ServerEndpoint("/echo")
public class EchoSocket {

	/**
	 * Handles receipt of the message and echos back the same message with
	 * "(from your server)" added.
	 * 
	 * @param message
	 *            message
	 * @param client
	 *            client
	 * @return echoed message.
	 */
	@OnMessage
	public String echo(final String message, final Session client) {
		return message + " (from your server)";
	}
}
