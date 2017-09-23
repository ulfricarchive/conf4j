package com.ulfric.conf4j.source;

import java.io.Reader;
import java.time.Instant;

import com.ulfric.conf4j.interpreter.DataType;

public interface Source {

	Reader read();

	DataType getType();

	Instant getLastChange();

}
