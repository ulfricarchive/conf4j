package com.ulfric.conf4j.interpreter.locale;

import com.ulfric.conf4j.interpreter.DataType;

import java.util.Collections;
import java.util.List;

public enum LocaleDataType implements DataType {

	INSTANCE;

	private final List<String> extension = Collections.singletonList("locale");

	@Override
	public List<String> getExtensions() {
		return extension;
	}

	@Override
	public String toString() {
		return "Locale";
	}

}
