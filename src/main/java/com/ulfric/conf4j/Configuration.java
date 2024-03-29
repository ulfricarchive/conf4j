package com.ulfric.conf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ulfric.conf4j.interpreter.Interpreter;
import com.ulfric.conf4j.interpreter.InterpreterProvider;
import com.ulfric.conf4j.reload.ReloadingStrategy;
import com.ulfric.conf4j.source.Source;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class Configuration {

	private static final Gson GSON = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
			.create();
	public static final Pattern CAPITALS_GROUP = Pattern.compile("([A-Z0-9]+)");

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private InterpreterProvider interpreter;
		private Source source;
		private ReloadingStrategy reloadingStrategy;

		private Builder() {}

		public Configuration build() {
			return new Configuration(interpreter, source, reloadingStrategy);
		}

		public Builder setInterpreter(InterpreterProvider interpreter) {
			this.interpreter = interpreter;
			return this;
		}

		public Builder setInterpreter(Interpreter interpreter) {
			return setInterpreter(new InterpreterProvider(interpreter));
		}

		public Builder setSource(Source source) {
			this.source = source;
			return this;
		}

		public Builder setReloadingStrategy(ReloadingStrategy reloadingStrategy) {
			this.reloadingStrategy = reloadingStrategy;
			return this;
		}
	}

	private final InterpreterProvider provider;
	private final Source source;
	private JsonObject data;
	private Map<String, Map<Type, Object>> parsedData;

	public Configuration(InterpreterProvider provider, Source source, ReloadingStrategy reloadingStrategy) {
		Objects.requireNonNull(provider, "provider");
		Objects.requireNonNull(source, "source");

		this.provider = provider;
		this.source = source;

		if (reloadingStrategy != null) {
			reloadingStrategy.register(this); // TODO unregister when closing
		}
	}

	public <T> T as(Class<? extends T> type) {
		validateConfigurationType(type);

		if (data == null) {
			reload();
		}

		if (isConfigurationBeanType(type)) {
			type = extend(type, new ArrayList<>());
		}

		return GSON.fromJson(data, type);
	}

	private void validateConfigurationType(Class<?> type) {
		Objects.requireNonNull(type, "type");

		if (!type.isInterface()) {
			throw new IllegalArgumentException(type + " must be an interface");
		}

		if (!Modifier.isPublic(type.getModifiers())) {
			throw new IllegalArgumentException(type + " must be public");
		}

		for (Method method : type.getMethods()) {
			validateConfigurationMethod(method);
		}
	}

	private void validateConfigurationMethod(Method method) {
		if (!isConfigurationMethod(method)) {
			return;
		}

		Class<?> declaring = method.getDeclaringClass();
		if (method.isDefault()) {
			throw new IllegalArgumentException(declaring + " has default method " + method);
			// TODO support default methods returning default values
		}

		Class<?> returnType = method.getReturnType();
		if (isExpresslyIllegalReturnType(returnType)) {
			throw new IllegalArgumentException(
			        declaring + " has method " + method + " with illegal return type " + returnType);
		}

		int modifiers = method.getModifiers();
		if (Modifier.isStatic(modifiers)) {
			throw new IllegalArgumentException(declaring + " has static method " + method);
		}

		if (Modifier.isSynchronized(modifiers)) {
			throw new IllegalArgumentException(declaring + " has synchronized method " + method);
		}
	}

	public boolean isConfigurationMethod(Method method) {
		Class<?> declaring = method.getDeclaringClass();
		return declaring != Object.class && declaring != ConfigurationBean.class;
	}

	private boolean isExpresslyIllegalReturnType(Class<?> returnType) {
		return returnType == Object.class || returnType == void.class || returnType == Void.class;
	}

	private <T> Class<? extends T> extend(Class<T> type, List<String> parentSections) {
		DynamicType.Builder<? extends T> builder = new ByteBuddy().subclass(type); // TODO Dragoon dynamic type

		builder = builder.implement(ConfigurationBean.class); // TODO reload hooks

		for (Method method : type.getMethods()) {
			if (isConfigurationMethod(method)) {
				Type returnType = extendAsStaticBean(method.getGenericReturnType());

				Class<?> actualType = getRawType(returnType);
				if (actualType == null) {
					throw new IllegalArgumentException("Illegal return type for " + method);
				}

				String name = method.getName();

				List<String> nextLayerParentSections = new ArrayList<>(parentSections);
				nextLayerParentSections.add(name);

				if (isConfigurationBeanType(actualType)) {
					returnType = extend(actualType, nextLayerParentSections); // TODO doesn't account for type params on the class
				}

				ConfigurationKey key = new ConfigurationKey(parentSections, name, returnType);
				builder = builder.defineMethod(name, returnType)
						.intercept(MethodDelegation.to(new ConfigurationElement(key)));
			}
		}

		return builder.make().load(type.getClassLoader()).getLoaded();
	}

	private Type[] extendAsStaticBeans(Type[] types) {
		return Arrays.stream(types)
			.map(this::extendAsStaticBean)
			.toArray(Type[]::new);
	}

	private Type extendAsStaticBean(Type type) {
		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;

			if (isConfigurationBeanType(clazz)) {
				DynamicType.Builder<?> builder = new ByteBuddy().subclass(clazz);

				for (Method method : clazz.getMethods()) {
					if (isConfigurationMethod(method)) {
						Type returnType = extendAsStaticBean(method.getGenericReturnType());

						String name = method.getName();
						builder = builder.defineField(name, returnType, Visibility.PRIVATE);
						builder = builder.defineMethod(name, returnType)
									.intercept(FieldAccessor.ofField(name));
					}
				}

				return builder.make().load(clazz.getClassLoader()).getLoaded();
			}

			return type;
		}

		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;

			Class<?> rawType = getRawType(parameterizedType.getRawType());
			if (rawType == null) {
				return null;
			}

			Type[] correctedReturnTypes = extendAsStaticBeans(parameterizedType.getActualTypeArguments());
			return TypeUtils.parameterize(rawType, correctedReturnTypes);
		}

		return type;
	}

	private boolean isConfigurationBeanType(Class<?> type) {
		return type.isInterface() && ConfigurationBean.class.isAssignableFrom(type);
	}

	private Class<?> getRawType(Type type) {
		if (type instanceof Class) {
			return (Class<?>) type;
		}

		if (type instanceof ParameterizedType) {
			return getRawType(((ParameterizedType) type).getRawType());
		}

		return null;
	}

	public void reload() {
		Interpreter interpreter = provider.apply(source.getType());
		Map<String, Object> dataAsMap = new HashMap<>();
		source.read().stream().map(interpreter::apply).filter(Objects::nonNull).forEach(dataAsMap::putAll); // TODO merge these instead of overriding
		data = (JsonObject) GSON.toJsonTree(dataAsMap);
		parsedData = new HashMap<>();
	}

	public class ConfigurationElement {
		private final ConfigurationKey key;

		public ConfigurationElement(ConfigurationKey key) {
			this.key = key;
		}

		@RuntimeType
		public Object intercept() {
			Map<Type, Object> parsedData =
					Configuration.this.parsedData.computeIfAbsent(key.getFullKey(), ignore -> new HashMap<>()); // TODO do we want to use identityhashmap?

			return parsedData.computeIfAbsent(key.getType(), type -> {
				JsonObject data = Configuration.this.data;
				if (data == null) {
					return null;
				}

				for (String part : key.getParentSections()) {
					JsonElement element = data.get(part);

					if (element == null || !element.isJsonObject()) {
						return null;
					}

					data = (JsonObject) element;
				}

				JsonElement parse = data.get(key.getName());
				if (parse == null) {
					return null;
				}

				return GSON.fromJson(parse, type);
			});
		}
	}

}
