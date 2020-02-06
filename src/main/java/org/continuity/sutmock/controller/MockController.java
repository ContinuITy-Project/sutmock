package org.continuity.sutmock.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.continuity.sutmock.entities.MockResponse;
import org.continuity.sutmock.logging.RequestLog;
import org.continuity.sutmock.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Henning Schulz
 *
 */
@RestController
public class MockController {

	private static final String RESTART_PATH = "/_restart";

	private static final String LOGS_PATH = "/_logs";

	private static final String STATUS_PATH = "/_status";

	private static final String GET = "GET";

	private static final String POST = "POST";

	@Autowired
	private SessionManager sessionManager;

	@Autowired
	private RequestLog requestLog;

	@Autowired
	private RestartService restartService;

	/**
	 * Accepts any request, logs it, and answers it with 200, except for the following cases:
	 *
	 * <ul>
	 * <li>request POST /_restart: resets and restarts the mock</li>
	 * <li>GET /_logs: returns all stored logs</li>
	 * <li>log buffer is full: answer is 500 and request will not be logged</li>
	 * </ul>
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@RequestMapping("**")
	public ResponseEntity<?> request(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException {
		if (RESTART_PATH.equals(request.getRequestURI()) && POST.equals(request.getMethod())) {
			return ResponseEntity.ok(restartService.restart());
		} else if (LOGS_PATH.equals(request.getRequestURI()) && GET.equals(request.getMethod())) {
			return ResponseEntity.ok(requestLog.getAllLogsAsString());
		} else if (STATUS_PATH.equals(request.getRequestURI()) && GET.equals(request.getMethod())) {
			return ResponseEntity.ok("up and running");
		}

		Cookie sessionCookie = sessionManager.getSessionCookie(request);
		boolean storedSuccessfully = requestLog.newRequest(request, sessionCookie.getValue());

		response.addCookie(sessionCookie);

		if (storedSuccessfully) {
			return ResponseEntity.ok(new MockResponse(request.getMethod(), request.getRequestURI(), sessionCookie.getValue()));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MockResponse(request.getMethod(), request.getRequestURI(), sessionCookie.getValue()));
		}
	}

}
