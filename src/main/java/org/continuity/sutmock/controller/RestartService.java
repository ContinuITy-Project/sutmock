package org.continuity.sutmock.controller;

import java.io.IOException;

import org.continuity.sutmock.logging.RequestLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.stereotype.Service;

/**
 *
 * @author Henning Schulz
 *
 */
@Service
public class RestartService {

	@Autowired
	private RestartEndpoint restartEndpoint;

	@Autowired
	private RequestLog requestLog;

	public Object restart() throws InterruptedException, IOException {
		requestLog.stop();
		requestLog.clear();

		return restartEndpoint.restart();
	}

}
