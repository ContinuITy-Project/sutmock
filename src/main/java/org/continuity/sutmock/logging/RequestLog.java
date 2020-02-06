package org.continuity.sutmock.logging;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.continuity.sutmock.entities.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Henning Schulz
 *
 */
@Component
public class RequestLog {

	public static final LogEntry STOP_ENTRY = new LogEntry("STOP", null);

	private static final Pattern DATE_PATTERN = Pattern.compile(".* \\[(.+)\\] .*");

	private final BlockingQueue<LogEntry> buffer;

	private final List<LogWriter> writers;

	private final ExecutorService executorService;

	@Autowired
	public RequestLog(@Value("${sutmock.buffersize:1024}") int bufferSize, @Value("${sutmock.writers:-1}") int numWriters, @Value("${sutmock.file_prefix:logs/log_}") String filePrefix)
			throws IOException {
		this.buffer = new ArrayBlockingQueue<>(bufferSize);

		if (numWriters < 1) {
			numWriters = Math.max(Runtime.getRuntime().availableProcessors() / 2, 1);
		}

		this.executorService = Executors.newFixedThreadPool(numWriters);
		this.writers = new ArrayList<>(numWriters);

		Paths.get(filePrefix).getParent().toFile().mkdirs();

		for (int i = 0; i < numWriters; i++) {
			LogWriter writer = new LogWriter(buffer, filePrefix + i + ".log");
			this.writers.add(writer);
			this.executorService.execute(writer);
		}
	}

	/**
	 * Adds a new request for processing.
	 *
	 * @param request
	 * @param sessionId
	 * @return {@code true} if the request was added successfully. {@code false} if the queue is
	 *         full and the request will be dropped.
	 */
	public boolean newRequest(HttpServletRequest request, String sessionId) {
		return buffer.offer(new LogEntry(sessionId, request));
	}

	public String getAllLogsAsString() {
		return writers.stream().map(LogWriter::getWrittenLines).flatMap(List::stream).sorted(this::compareLogLines).collect(Collectors.joining(System.lineSeparator()));
	}

	public void stop() throws InterruptedException {
		for (@SuppressWarnings("unused")
		LogWriter w : writers) {
			buffer.put(STOP_ENTRY);
		}

		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.MINUTES);
	}

	public void clear() throws IOException {
		for (LogWriter writer : writers) {
			writer.deleteFile();
		}

		buffer.clear();
	}

	private int compareLogLines(String first, String second) {
		return extractDateFromLogLine(first).compareTo(extractDateFromLogLine(second));
	}

	private LocalDateTime extractDateFromLogLine(String line) {
		Matcher matcher = DATE_PATTERN.matcher(line);

		if (matcher.matches()) {
			return LocalDateTime.parse(matcher.group(1));
		} else {
			return LocalDateTime.MAX;
		}
	}

}
