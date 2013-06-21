package net.trajano.examples.websockets.web;

import static javax.json.Json.createObjectBuilder;
import static javax.json.Json.createReader;

import java.io.IOException;
import java.io.Reader;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * This endpoint registers clients and provides a list of connected clients.
 * 
 * @author Archimedes Trajano
 */
@ServerEndpoint("/chat")
@Stateless
public class ChatSocket {

	@Inject
	private ClientList clientList;

	@OnClose
	public void deregisterClient(final Session session) {
		clientList.removeSession(session.getId());
	}

	/**
	 * Returns a list of clients that are registered.
	 * 
	 * @param message
	 *            ignored.
	 */
	@OnMessage
	public void echo(final Reader messageReader, final Session client)
			throws IOException {
		final JsonObject message;
		try {
			message = createReader(messageReader).readObject();
		} catch (final Throwable e) {
			e.printStackTrace();
			return;
		}
		if (message.containsKey("text")) {
			final String messageJson = createObjectBuilder()
					.add("text", message.getString("text"))
					.add("from", client.getId()).build().toString();
			if (message.containsKey("to")) {
				final Session targetSession = clientList.getSession(message
						.getString("to"));
				if (targetSession != null && targetSession.isOpen()) {
					targetSession.getBasicRemote().sendText(messageJson);
					// send message to specific client synchronously
				}
			} else {
				// send broadcast message asynchronously
				for (final Session otherSession : clientList.getSessions()) {
					otherSession.getAsyncRemote().sendText(messageJson);
				}
			}
		}
	}

	@OnOpen
	public void registerClient(final Session session) throws IOException {
		clientList.addSession(session);
	}
}
