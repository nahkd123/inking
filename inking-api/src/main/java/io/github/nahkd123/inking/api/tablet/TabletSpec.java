package io.github.nahkd123.inking.api.tablet;

import java.util.Optional;

import io.github.nahkd123.inking.api.util.Vector2;

/**
 * <p>
 * An interface for a bunch of tablet's specifications.
 * </p>
 */
public interface TabletSpec {
	/**
	 * <p>
	 * Get the display name of this tablet, like "Wacom CTC-6110WL" or "Windows Ink"
	 * for example.
	 * </p>
	 * 
	 * @return The display name of tablet.
	 */
	public String getTabletName();

	public TabletType getTabletType();

	/**
	 * <p>
	 * Get the maximum raw pressure value that the tablet can sense. The reported
	 * value from packet ({@link Packet#getRawPressure()}) should never be greater
	 * than the returned value of this method.
	 * </p>
	 * <p>
	 * For example, a typical consumer's tablet with 1024 pressure points will
	 * reports 1023 as maximum pressure value.
	 * </p>
	 * 
	 * @return Maximum pressure value.
	 */
	public int getMaxPressure();

	/**
	 * <p>
	 * Get the physical size of this tablet. If {@link #getTabletType()} returns
	 * {@link TabletType#WINDOW_BOUND}, this will always return
	 * {@link Optional#empty()}.
	 * </p>
	 * 
	 * @return The physical size of this tablet.
	 */
	public Optional<Vector2> getPhysicalSize();

	/**
	 * <p>
	 * Get the maximum input value of this tablet. The underlying application should
	 * never map inputs if {@link #getTabletType()} returns
	 * {@link TabletType#WINDOW_BOUND}.
	 * </p>
	 * <p>
	 * Values from {@link Packet#getPenPosition()} will always be within the limit
	 * defined from returned value of this method.
	 * </p>
	 * 
	 * @return The input size of this tablet.
	 */
	public Vector2 getInputSize();

	/**
	 * <p>
	 * Get the number of buttons from the pen or tablet.
	 * </p>
	 * 
	 * @param type The button type, a.k.a where the buttons are located.
	 * @return Number of buttons.
	 */
	public int getButtonsCount(ButtonType type);
}
