package io.github.nahkd123.inking.api.tablet;

import java.util.Optional;

import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.Vector2;

/**
 * <p>
 * Tablet information. Values reported from getters in this interface should
 * never be changed throughout the entire application's lifetime. In other
 * words, all getters should returns constant values.
 * </p>
 */
public interface TabletInfo {
	/**
	 * <p>
	 * Get the display name for this tablet. This method returns either the device
	 * name from manufacturer or the tablet's serial ID. It will never returns
	 * {@code null}.
	 * </p>
	 * 
	 * @return Display name of this tablet.
	 */
	public String getTabletName();

	/**
	 * <p>
	 * The maximum pressure that this tablet can reports. This is also the maximum
	 * value for {@link Packet#getRawPressure()}. The value is unitless.
	 * </p>
	 * 
	 * @return The max raw pressure.
	 */
	public int getMaxPressure();

	/**
	 * <p>
	 * Get the physical size of this tablet.
	 * </p>
	 * 
	 * @return The physical size of this tablet.
	 */
	public Optional<Vector2> getPhysicalSize();

	/**
	 * <p>
	 * Get the input rectangle size of this tablet. Pen's position reported from the
	 * tablet should be within the size reported from this getter. An empty result
	 * may be returned if the driver can't get the absolute positon of the pen on
	 * tablet's surface, such as Windows Ink integration/Linux libinput.
	 * </p>
	 * <p>
	 * The measurement unit is always {@link MeasurementUnit#UNITLESS}.
	 * </p>
	 * 
	 * @return The input rectangle size.
	 */
	public Optional<Vector2> getInputSize();

	/**
	 * <p>
	 * Get a number of buttons that this tablet can report. OpenTabletDriver
	 * currently have a cap of 32 buttons for each type, due to 32-bit integer
	 * limit. This can be raised in the future, but seriously, why are you using a
	 * tablet/pen with more than 20 buttons?
	 * </p>
	 * 
	 * @param type The button type. {@link ButtonType#PEN} for buttons located on
	 *             the pen, and {@link ButtonType#AUXILIARY} for anywhere else.
	 * @return The number of buttons for given button type.
	 */
	public int getButtonsCount(ButtonType type);
}
