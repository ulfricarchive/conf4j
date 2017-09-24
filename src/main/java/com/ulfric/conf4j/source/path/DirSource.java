package com.ulfric.conf4j.source.path;

import com.ulfric.conf4j.interpreter.DataType;
import com.ulfric.conf4j.source.MultiSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class DirSource extends MultiSource {

	public static List<PathSource> getAllSources(Path directory) {
		try {
			return Files.list(directory)
				.filter(Files::isRegularFile)
				.map(PathSource::new)
				.collect(Collectors.toList());
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	public DirSource(DataType type, Path directory) {
		super(type, getAllSources(directory));
	}

}
