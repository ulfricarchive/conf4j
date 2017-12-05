package com.ulfric.conf4j.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.ulfric.conf4j.interpreter.json.JsonDataType;
import com.ulfric.conf4j.interpreter.locale.LocaleDataType;
import com.ulfric.conf4j.interpreter.yaml.YamlDataType;

public class DataTypes {

	private static final Map<String, DataType> TYPES_BY_STRING = new HashMap<>();

	static {
		register(NoDataType.INSTANCE);
		register(JsonDataType.INSTANCE);
		register(YamlDataType.INSTANCE);
		register(LocaleDataType.INSTANCE);
	}

	public static void register(DataType type) {
		Objects.requireNonNull(type, "type");

		type.getExtensions()
			.forEach(name -> TYPES_BY_STRING.put(name, type));
	}

	public static void unregister(DataType type) {
		Objects.requireNonNull(type, "type");

		type.getExtensions()
			.forEach(name -> TYPES_BY_STRING.remove(name, type));
	}

	public static DataType get(String type) {
		Objects.requireNonNull(type, "type");

		return TYPES_BY_STRING.getOrDefault(type, NoDataType.INSTANCE);
	}

	private DataTypes() {
	}

}
