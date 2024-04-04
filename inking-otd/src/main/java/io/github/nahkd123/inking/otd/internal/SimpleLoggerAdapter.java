package io.github.nahkd123.inking.otd.internal;

import org.slf4j.Logger;

import io.github.nahkd123.inking.internal.SimpleLogger;

public class SimpleLoggerAdapter implements SimpleLogger {
	private Logger logger;

	public SimpleLoggerAdapter(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void warning(String message) {
		logger.warn(message);
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}
}
