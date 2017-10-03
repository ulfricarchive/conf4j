package com.ulfric.conf4j.interpreter;

import java.util.Collections;
import java.util.List;

public enum NoDataType implements DataType {

	INSTANCE;

	@Override
	public List<String> getExtensions() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return "NoDataType";
	}

}
