package com.ulfric.conf4j.interpreter;

public class InterpreterNotFoundException extends IllegalArgumentException {

	private final DataType type;

	public InterpreterNotFoundException(DataType type) {
		super(String.valueOf(type));

		this.type = type;
	}

	public DataType getType() {
		return type;
	}

}
