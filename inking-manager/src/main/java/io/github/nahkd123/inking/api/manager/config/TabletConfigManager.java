package io.github.nahkd123.inking.api.manager.config;

import java.util.List;

import io.github.nahkd123.inking.api.tablet.Tablet;

/**
 * <p>
 * This is the manager to manage all user's configurations. When initializing
 * the manager, the implementation must load all configurations in the storage,
 * including disconnected device (but have configuration locally).
 * </p>
 */
public interface TabletConfigManager {
	/**
	 * <p>
	 * Get (or create default) tablet configuration from given tablet handle. The
	 * default tablet configuration can be obtained by using
	 * {@link TabletConfig#createDefault(Tablet)}.
	 * </p>
	 * <p>
	 * Modifications to the {@link TabletConfig} and its children (eg:
	 * {@link AreaConfig}) will be applied immediately across all places that uses
	 * the same {@link TabletConfigManager}, but it will not be saved to storage. To
	 * save configuration to storage, use {@link #save(TabletConfig)}.
	 * </p>
	 * 
	 * @param tablet The tablet handle.
	 * @return New or existing tablet configuration.
	 */
	public TabletConfig get(Tablet tablet);

	/**
	 * <p>
	 * Save the configuration to storage.
	 * </p>
	 * 
	 * @param config The tablet configuration.
	 */
	public void save(TabletConfig config);

	/**
	 * <p>
	 * Get all tablet configurations, including disconnected devices.
	 * </p>
	 * 
	 * @return List of all configurations.
	 */
	public List<TabletConfig> getAll();
}
