package com.ulfric.conf4j.source;

import com.ulfric.conf4j.interpreter.DataType;

import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MultiSource implements Source {

	private final DataType type;
	private final List<? extends Source> sources;

	public MultiSource(DataType type, List<? extends Source> sources) {
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(sources, "sources");

		this.type = type;
		this.sources = new ArrayList<>(sources);
	}

	@Override
	public List<Reader> read() {
		return sources.stream()
				.map(Source::read)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	@Override
	public DataType getType() {
		return type;
	}

	@Override
	public Instant getLastChange() {
		return sources.stream()
				.map(Source::getLastChange)
				.sorted((o1, o2) -> o2.compareTo(o1))
				.findFirst()
				.orElse(Instant.MIN);
	}

}
