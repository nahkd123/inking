package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.util.Vector2;

/**
 * <p>
 * An interface for a bunch of tablet's specifications.
 * </p>
 */
public interface TabletSpec {
	/**
	 * <p>
	 * Get the display name of this tablet, like "Wacom CTC-6110WL" or "Windows Ink
	 * Window" for example.
	 * </p>
	 * 
	 * @return The display name of tablet.
	 */
	public String getTabletName();

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
	 * Get the physical size of this tablet. The width of the tablet is
	 * {@link Vector2#x()} and the height is {@link Vector2#y()}. Both width and
	 * height are measured in millimeters.
	 * </p>
	 * <p>
	 * Please note that the physical size in here implies the physical input area,
	 * not the size of entire tablet. For Windows Ink, the physical size is the
	 * physical size of primary monitor (because I don't know how to handle this!).
	 * </p>
	 * 
	 * @return The physical input size.
	 */
	public Vector2 getPhysicalSize();

	/**
	 * <p>
	 * Get the input size of this tablet. Position values reported from
	 * {@link Packet#getPenPosition()} are always inside this input size. Windows
	 * Ink driver may restrict the position to be inside
	 * {@link Tablet#getInputRectangle()}.
	 * </p>
	 * <p>
	 * For Windows Ink, the size is the size of primary monitor. You should use
	 * {@link Tablet#getInputRectangle()} to check the maximum values.
	 * </p>
	 * 
	 * @return The packet input size.
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
