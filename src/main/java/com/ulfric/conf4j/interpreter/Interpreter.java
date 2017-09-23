package com.ulfric.conf4j.interpreter;

import java.io.Reader;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Interpreter extends Predicate<DataType>, Function<Reader, Map<String, Object>> {

}
