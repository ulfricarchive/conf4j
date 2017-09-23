package com.ulfric.conf4j.source.string;

import com.ulfric.conf4j.interpreter.DataType;
import com.ulfric.conf4j.source.Source;

import java.io.Reader;
import java.io.StringReader;
import java.time.Instant;
import java.util.Objects;

public class StringSource implements Source {

	private final String data;
	private final DataType type;
	private final Instant creation = Instant.now();

	public StringSource(String data, DataType type) {
		Objects.requireNonNull(data, "data");
		Objects.requireNonNull(type, "type");

		this.data = data;
		this.type = type;
	}

	@Override
	public Reader read() {
		return new StringReader(data);
	}

	@Override
	public DataType getType() {
		return type;
	}

	@Override
	public Instant getLastChange() {
		return creation;
	}

}