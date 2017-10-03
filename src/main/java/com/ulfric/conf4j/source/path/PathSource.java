package com.ulfric.conf4j.source.path;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ulfric.conf4j.interpreter.DataType;
import com.ulfric.conf4j.interpreter.DataTypes;
import com.ulfric.conf4j.interpreter.NoDataType;
import com.ulfric.conf4j.source.Source;

public class PathSource implements Source {

	private final Path path;

	public PathSource(Path path) {
		Objects.requireNonNull(path, "path");

		this.path = path;
	}

	@Override
	public List<Reader> read() {
		try {
			if (Files.notExists(path)) {
				return Collections.singletonList(new StringReader(""));
			}

			return Collections.singletonList(Files.newBufferedReader(path));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	@Override
	public DataType getType() {
		Path fileName = path.getFileName();

		if (fileName == null) {
			return NoDataType.INSTANCE;
		}

		String name = fileName.toString();
		int dot = name.lastIndexOf('.');
		if (dot == -1) {
			return NoDataType.INSTANCE;
		}

		return DataTypes.get(name.substring(dot + 1));
	}

	@Override
	public Instant getLastChange() {
		try {
			return Files.getLastModifiedTime(path).toInstant();
		} catch (IOException exception) {
			return Instant.MIN;
		}
	}

	@Override
	public String getName() {
		return path.toString();
	}

}
