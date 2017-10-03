package com.ulfric.conf4j;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

final class ConfigurationKey {

	public static final Pattern CAPITALS_GROUP = Pattern.compile("([A-Z0-9]+)");

	public static String hyphenate(String name) {
		return CAPITALS_GROUP.matcher(name).replaceAll("-$0").toLowerCase();
	}

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

		StringJoiner joiner = new StringJoiner(".");
		parentSections.stream().map(ConfigurationKey::hyphenate).forEach(joiner::add);
		joiner.add(hyphenate(name));
		this.fullKey = joiner.toString();

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
