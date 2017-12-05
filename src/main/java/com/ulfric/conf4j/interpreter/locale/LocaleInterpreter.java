package com.ulfric.conf4j.interpreter.locale;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.ulfric.conf4j.interpreter.DataType;
import com.ulfric.conf4j.interpreter.Interpreter;

public class LocaleInterpreter implements Interpreter {

	@Override
	public boolean test(DataType type) {
		return type == LocaleDataType.INSTANCE;
	}

	@Override
	public Map<String, Object> apply(Reader reader) {
		Map<String, Object> data = new HashMap<>();

		for (String line : readLines(reader)) {
			int splitOn = line.indexOf('=');
			if (splitOn == -1) {
				continue;
			}

			String key = StringUtils.stripEnd(line.substring(0, splitOn), null);
			String value = StringUtils.stripStart(line.substring(splitOn + 1), null);

			data.put(key, value);
		}

		Map<String, Object> wrapped = new HashMap<>();
		wrapped.put("messages", data);
		return wrapped;
	}

	private List<String> readLines(Reader reader) {
		try {
			return IOUtils.readLines(reader);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

}
