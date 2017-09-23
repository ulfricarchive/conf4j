package com.ulfric.conf4j.interpreter.json;

import java.io.Reader;
import java.util.Map;

import com.google.gson.Gson;
import com.ulfric.conf4j.interpreter.DataType;
import com.ulfric.conf4j.interpreter.Interpreter;

public class JsonInterpreter implements Interpreter {

	private static final Gson GSON = new Gson();

	@Override
	public boolean test(DataType type) {
		return type == JsonDataType.INSTANCE;
	}

	@Override
	public Map<String, Object> apply(Reader reader) {
		return GSON.fromJson(reader, Map.class);
	}

}
