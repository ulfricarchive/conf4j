package com.ulfric.conf4j.interpreter.json;

import com.ulfric.conf4j.interpreter.DataType;

public enum JsonDataType implements DataType {

	INSTANCE;

	@Override
	public String toString() {
		return "json";
	}

}
