package io.github.nahkd123.inking.api.manager.config;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record ConfigurableValue<S, T>(Class<T> type, String name, String description, Function<S, T> getter, BiConsumer<S, T> setter) implements Configurable {
	public ConfigurableValue(Class<T> type, String name, Function<S, T> getter, BiConsumer<S, T> setter) {
		this(type, name, null, getter, setter);
	}

	public ConfigurableValue(Class<T> type, Function<S, T> getter, BiConsumer<S, T> setter) {
		this(type, type.getClass().getName(), null, getter, setter);
	}

	@Override
	public String getName() { return name; }

	@Override
	public String getDescription() { return description; }
}
