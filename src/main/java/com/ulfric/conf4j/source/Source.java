package com.ulfric.conf4j.source;

import com.ulfric.conf4j.interpreter.DataType;

import java.io.Reader;
import java.time.Instant;
import java.util.List;

public interface Source {

	List<Reader> read();

	DataType getType();

	Instant getLastChange();

}
