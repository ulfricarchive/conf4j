package com.ulfric.conf4j.interpreter;

import com.ulfric.conf4j.interpreter.json.JsonInterpreter;
import com.ulfric.conf4j.interpreter.yaml.YamlInterpreter;

public class StandardInterpreterProvider extends InterpreterProvider {

	public StandardInterpreterProvider() {
		super(new JsonInterpreter(), new YamlInterpreter());
	}

}
