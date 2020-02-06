package org.continuity.sutmock.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.continuity.sutmock.entities.LogEntry;

/**
 *
 * @author Henning Schulz
 *
 */
public class LogWriter implements Runnable {

	private final BlockingQueue<LogEntry> buffer;

	private final String filename;

	private final FileWriter writer;

	private boolean running = false;

	public LogWriter(BlockingQueue<LogEntry> buffer, String filename) throws IOException {
		this.buffer = buffer;
		this.filename = filename;
		this.writer = new FileWriter(filename);
	}

	@Override
	public void run() {
		running = true;

		try {
			while (true) {
				LogEntry entry = buffer.take();

				if (entry == RequestLog.STOP_ENTRY) {
					break;
				}

				processEntry(entry);
			}
		} catch (InterruptedException | IOException e) {
			System.err.println("Stopping thread due to " + e);
			e.printStackTrace();
		}

		shutdown();
	}

	public List<String> getWrittenLines() {
		try {
			if (running) {
				writer.flush();
			}

			return Files.readAllLines(Paths.get(filename));
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public void deleteFile() throws IOException {
		if (running) {
			throw new IllegalStateException("Cannot delete file of running thread!");
		}

		Files.deleteIfExists(Paths.get(filename));
	}

	private void processEntry(LogEntry entry) throws IOException {
		if (entry != null) {
			writer.write(entry.toString());
			writer.write(System.lineSeparator());
		}
	}

	private void shutdown() {
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		running = false;
	}

}
