package com.ulfric.conf4j;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class ConfigurationKey {

	private final List<String> parentSections;
	private final String name;
	private final String fullKey;
	private final Type type;

	public ConfigurationKey(List<String> parentSections, String name, Type type) {
		Objects.requireNonNull(parentSections, "parentSections");
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(type, "type");

		this.parentSections = Collections.unmodifiableList(new ArrayList<>(parentSections));
		this.name = name;

		String fullKey = parentSections.stream().collect(Collectors.joining("."));
		if (!fullKey.isEmpty()) {
			fullKey = fullKey + '.';
		}
		fullKey = fullKey + name;
		this.fullKey = fullKey;

		this.type = type;
	}

	public List<String> getParentSections() {
		return parentSections;
	}

	public String getName() {
		return name;
	}

	public String getFullKey() {
		return fullKey;
	}

	@Override
	public String toString() {
		return fullKey;
	}

	public Type getType() {
		return type;
	}

}
