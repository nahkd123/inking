package io.github.nahkd123.inking.api.tablet;

public enum TabletType {
	/**
	 * <p>
	 * Physical tablet device provides full access to the tablet area. The reported
	 * coordinates are based on {@link TabletSpec#getInputSize()}.
	 * </p>
	 */
	PHYSICAL,
	/**
	 * <p>
	 * The input coming from the driver is bounds to a specific window.
	 * </p>
	 */
	WINDOW_BOUND,
	/**
	 * <p>
	 * Unknown tablet type.
	 * </p>
	 */
	UNKNOWN;
}
