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

	private final HttpServletRequest request;

	public LogEntry(String sessionId, HttpServletRequest request) {
		this.timestamp = LocalDateTime.now();
		this.sessionId = sessionId;
		this.request = request;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * sessionId - - [timestamp] "method path protocol" responseCode -
	 */
	@Override
	public String toString() {
		if (request == null) {
			return sessionId;
		}

		return new StringBuilder().append(sessionId).append(" - - [").append(timestamp).append("] \"").append(request.getMethod()).append(" ").append(request.getRequestURI()).append(" ")
				.append(request.getProtocol()).append("\" 200 -").toString();
	}

}
