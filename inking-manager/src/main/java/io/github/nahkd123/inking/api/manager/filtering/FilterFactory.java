package io.github.nahkd123.inking.api.manager.filtering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.nahkd123.inking.api.manager.config.Configurable;

public interface FilterFactory<T extends TabletFilter<T>> {
	/**
	 * <p>
	 * Create a new filter with default parameters.
	 * </p>
	 * 
	 * @return A new filter.
	 */
	public TabletFilter<T> createDefaultFilter();

	/**
	 * <p>
	 * Get a list of configurable handles. The handles will be used for
	 * saving/loading filter configurations, as well as displaying the configurable
	 * handles in UI.
	 * </p>
	 * 
	 * @param filter The filter to obtain configuration handles.
	 * @return List of configuration handles. Can be {@code null}. Default behavior
	 *         is returning {@code null}, which means this filter doesn't have
	 *         anything to configure.
	 */
	default List<Configurable> getFilterConfig(T filter) {
		return null;
	}

	public static final Map<String, FilterFactory<?>> ID_TO_FACTORY = new HashMap<>();
	public static final Map<FilterFactory<?>, String> FACTORY_TO_ID = new HashMap<>();

	default void register(String id) {
		if (ID_TO_FACTORY.containsKey(id)) throw new IllegalStateException("Filter factory already registered: " + id);
		ID_TO_FACTORY.put(id, this);
		FACTORY_TO_ID.put(this, id);
	}

	default String getId() { return FACTORY_TO_ID.get(this); }
}
