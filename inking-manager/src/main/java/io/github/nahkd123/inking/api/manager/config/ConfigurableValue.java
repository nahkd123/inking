package io.github.nahkd123.inking.api.manager.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * A configurable value handle where the configuration UI can get or set the
 * value to the owning object.
 * </p>
 * <p>
 * <b>Mutable type</b>: Any changes on mutable type will be applied directly to
 * the object obtained from the getter, which means the setter will remains
 * unused.
 * </p>
 * 
 * @param <T> The value type.
 */
public record ConfigurableValue<T>(Class<T> type, String name, String description, Supplier<T> getter, Consumer<T> setter) implements Configurable {
	public ConfigurableValue(Class<T> type, String name, Supplier<T> getter, Consumer<T> setter) {
		this(type, name, null, getter, setter);
	}

	public ConfigurableValue(Class<T> type, Supplier<T> getter, Consumer<T> setter) {
		this(type, type.getClass().getName(), null, getter, setter);
	}

	@Override
	public String getName() { return name; }

	@Override
	public String getDescription() { return description; }
}
