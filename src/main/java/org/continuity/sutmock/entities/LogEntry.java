package org.continuity.sutmock.entities;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Henning Schulz
 *
 */
public class LogEntry {

	private final LocalDateTime timestamp;

	private final String sessionId;

	private final String method;

	private final String uri;

	private final String protocol;

	public LogEntry(String sessionId, HttpServletRequest request) {
		this.timestamp = LocalDateTime.now();
		this.sessionId = sessionId;
		this.method = request.getMethod();
		this.uri = request.getRequestURI();
		this.protocol = request.getProtocol();
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public String getProtocol() {
		return protocol;
	}

	/**
	 * sessionId - - [timestamp] "method path protocol" responseCode -
	 */
	@Override
	public String toString() {
		return new StringBuilder().append(sessionId).append(" - - [").append(timestamp).append("] \"").append(method).append(" ").append(uri).append(" ").append(protocol).append("\" 200 -")
				.toString();
	}

}
