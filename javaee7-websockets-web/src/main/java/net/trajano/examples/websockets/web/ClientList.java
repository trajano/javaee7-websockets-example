package net.trajano.examples.websockets.web;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.json.JsonArrayBuilder;
import javax.websocket.Session;

@Singleton
public class ClientList {

	/**
	 * Map of web socket sessions.
	 */
	private final Map<String, Session> sessions = new HashMap<>();

	public void addSession(final Session session) {
		sessions.put(session.getId(), session);
		broadcastSessionIds();
	}

	private void broadcastSessionIds() {
		final JsonArrayBuilder sessionIdArrayBuilder = createArrayBuilder();
		for (final String sessionId : sessions.keySet()) {
			sessionIdArrayBuilder.add(sessionId);
		}

		final String sessionIdsJson = createObjectBuilder()
				.add("sessionIds", sessionIdArrayBuilder).build().toString();
		for (final Session otherSession : sessions.values()) {
			if (otherSession.isOpen()) {
				otherSession.getAsyncRemote().sendText(sessionIdsJson);
			}
		}
	}

	public Session getSession(final String sessionId) {
		return sessions.get(sessionId);

	}

	public Collection<Session> getSessions() {
		return sessions.values();
	}

	public void removeSession(final String sessionId) {
		sessions.remove(sessionId);
		broadcastSessionIds();
	}

}
