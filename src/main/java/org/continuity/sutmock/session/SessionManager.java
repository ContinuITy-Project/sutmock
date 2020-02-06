package org.continuity.sutmock.session;

import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

/**
 *
 * @author Henning Schulz
 *
 */
@Component
public class SessionManager {

	private static final String SESSION_COOKIE = "session_id";

	public Cookie getSessionCookie(HttpServletRequest request) {
		Cookie sessionCookie = extractSessionCookie(request);

		return sessionCookie == null ? new Cookie(SESSION_COOKIE, freshSessionId()) : sessionCookie;
	}

	private Cookie extractSessionCookie(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (SESSION_COOKIE.equals(cookie.getName())) {
					return cookie;
				}
			}
		}

		return null;
	}

	private String freshSessionId() {
		return Long.toHexString(ThreadLocalRandom.current().nextLong());
	}

}
