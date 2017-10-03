package com.ulfric.conf4j.interpreter.yaml;

import com.ulfric.conf4j.interpreter.DataType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum YamlDataType implements DataType {

	INSTANCE;

	private final List<String> extensions = Collections.unmodifiableList(Arrays.asList("yaml", "yml"));

	@Override
	public List<String> getExtensions() {
		return extensions;
	}

	@Override
	public String toString() {
		return "Yaml";
	}

}
