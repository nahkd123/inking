package io.github.nahkd123.inking.api.manager.config;

import java.util.Map;

import io.github.nahkd123.inking.api.tablet.Tablet;

public interface TabletConfigManager {
	/**
	 * <p>
	 * Get (or create) tablet configuration from given tablet. By default,
	 * configurations created from this method will not be saved. Use
	 * {@link #save(TabletConfig)} to save the configuration.
	 * </p>
	 * <p>
	 * Modifications on {@link TabletConfig} applies globally, but it must be saved
	 * if you want to load your modifications next time the application is loaded.
	 * </p>
	 * 
	 * @param tablet The tablet.
	 * @return The tablet configuration.
	 */
	public TabletConfig get(Tablet tablet);

	/**
	 * <p>
	 * Save the tablet configuration.
	 * </p>
	 * 
	 * @param config The tablet configuration.
	 */
	public void save(TabletConfig config);

	/**
	 * <p>
	 * Get all configurations from this manager. The returned map is not modifiable.
	 * </p>
	 * 
	 * @return All configurations, with keys for tablet ID and values for
	 *         configurations.
	 */
	public Map<String, TabletConfig> getAllConfig();
}
