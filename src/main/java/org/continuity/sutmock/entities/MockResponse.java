package org.continuity.sutmock.entities;

/**
 *
 * @author Henning Schulz
 *
 */
public class MockResponse {

	private final String method;

	private final String path;

	private final String session;

	public MockResponse(String method, String path, String session) {
		this.method = method;
		this.path = path;
		this.session = session;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getSession() {
		return session;
	}

}
