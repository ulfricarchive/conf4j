package com.ulfric.conf4j.interpreter.json;

import com.ulfric.conf4j.interpreter.DataType;

import java.util.Collections;
import java.util.List;

public enum JsonDataType implements DataType {

	INSTANCE;

	private final List<String> extension = Collections.singletonList("json");

	@Override
	public List<String> getExtensions() {
		return extension;
	}

	@Override
	public String toString() {
		return "Json";
	}

}
