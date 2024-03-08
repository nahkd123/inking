package io.github.nahkd123.inking.api.manager.config;

/**
 * <p>
 * A handle for configurable values. Since we don't have access to (or we don't
 * want to include) DataFixerUpper's Codec, we need to find another way to make
 * it easy to save into file, as well as making it easy to build configuration
 * UI.
 * </p>
 */
public interface Configurable {
	public String getName();

	public String getDescription();
}
