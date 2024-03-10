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
	 * Get the display name of this tablet filter.
	 * </p>
	 * 
	 * @return The filter name.
	 */
	default String getFilterName() { return this.getClass().getName(); }

	/**
	 * <p>
	 * Get a list of configurable handles. The handles will be used for
	 * saving/loading filter configurations, as well as displaying the configurable
	 * handles in UI.
	 * </p>
	 * <p>
	 * If the returned value is {@code null}, the UI should indicate that this
	 * filter can't be configured. If the returned value is an empty list, the UI
	 * would still display filter configuration screen.
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

	/**
	 * <p>
	 * Register this factory to global filter factory registry.
	 * </p>
	 * 
	 * @param id The ID of the factory to register. The ID should follows the
	 *           hierarchical format, such as {@code inking/my_filter}, where
	 *           {@code inking} is the application/library name, and
	 *           {@code my_filter} is the name of filter.
	 */
	default void register(String id) {
		if (ID_TO_FACTORY.containsKey(id)) throw new IllegalStateException("Filter factory already registered: " + id);
		ID_TO_FACTORY.put(id, this);
		FACTORY_TO_ID.put(this, id);
	}

	default String getId() { return FACTORY_TO_ID.get(this); }

	/**
	 * <p>
	 * Register default filters. Can be called once only.
	 * </p>
	 */
	public static void registerDefaults() {
		new PressureMapperFilterFactory().register("inking/pressure");
	}
}
