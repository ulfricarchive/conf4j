package com.ulfric.conf4j.interpreter.yaml;

import com.ulfric.conf4j.interpreter.DataType;

public enum YamlDataType implements DataType {

	INSTANCE;

	@Override
	public String toString() {
		return "yaml";
	}

}
