package com.ulfric.conf4j.interpreter.yaml;

import java.io.Reader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.ulfric.conf4j.interpreter.DataType;
import com.ulfric.conf4j.interpreter.Interpreter;

public class YamlInterpreter implements Interpreter {

	private static final Yaml YAML = new Yaml();

	@Override
	public boolean test(DataType type) {
		return type == YamlDataType.INSTANCE;
	}

	@Override
	public Map<String, Object> apply(Reader reader) {
		return YAML.loadAs(reader, Map.class);
	}

}
