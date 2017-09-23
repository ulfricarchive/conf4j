package com.ulfric.conf4j.interpreter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InterpreterProvider implements Function<DataType, Interpreter> {

	private final List<Interpreter> interpreters;

	public InterpreterProvider(Interpreter... interpreters) {
		Objects.requireNonNull(interpreters, "interpreters");

		this.interpreters = Arrays.stream(interpreters).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public Interpreter apply(DataType key) {
		return interpreters.stream()
				.filter(interpreter -> interpreter.test(key))
				.findFirst()
				.orElseThrow(() -> new InterpreterNotFoundException(key));
	}

}
