package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.util.Emitter;
import io.github.nahkd123.inking.api.util.Vector2;

public interface Tablet {
	public TabletDriver getDriver();

	/**
	 * <p>
	 * Get the tablet unique ID. The ID contains the driver ID and the tablet unique
	 * ID from the driver. For example: "OpenTabletDriver:SERIAL_NUMBER" or
	 * "WindowsInk:WindowsInk". This value is safe to use for identifying the
	 * tablet.
	 * </p>
	 * 
	 * @return The tablet unique ID.
	 */
	public String getTabletId();

	/**
	 * <p>
	 * Get the connect state of this tablet. Note that the tablet object will still
	 * be available in the memory when the device is disconnected.
	 * </p>
	 * 
	 * @return Connect state.
	 */
	public boolean isConnected();

	/**
	 * <p>
	 * Get the tablet specifications. This includes the tablet's display name, its
	 * physical size and the packet input size.
	 * </p>
	 * 
	 * @return The tablet specificatons.
	 */
	public TabletSpec getSpec();

	/**
	 * <p>
	 * Get the input rectangle size. The value from {@link Packet#getPenPosition()}
	 * will always be within the bounds of input rectangle while the pen is
	 * hovering.
	 * </p>
	 * <p>
	 * <b>Windows Ink</b>: The size of the input rectangle is the size of the
	 * current window that the driver is bounds to. The measurement unit for each
	 * dimension is pixels.
	 * </p>
	 * <p>
	 * <b>OpenTabletDriver</b>: OTD reports entire tablet input rectangle. The unit
	 * is depends on which tablet you are using. Wacom tablets for example reports
	 * the physical size of the tablet, multiplied by {@code 10}.
	 * </p>
	 * 
	 * @return The input rectangle.
	 */
	public Vector2 getInputRectangle();

	/**
	 * <p>
	 * Get the emitter that emits when the tablet state is changed. It will emits
	 * when tablet is connected (or disconnected) from the driver.
	 * </p>
	 * 
	 * @return The state changes emitter.
	 */
	public Emitter<Tablet> getStateChangesEmitter();

	/**
	 * <p>
	 * Get the packets emitter for this tablet.
	 * </p>
	 * 
	 * @return The packets emitter.
	 * @implNote The emitted packets must be safe to be stored around in JVM. In
	 *           other words, it shouldn't be tied to native object. The
	 *           implementation must be aware that the packet emitted from this
	 *           emitter have a chance to be reused. For example, a client code may
	 *           store previous packet info so that it can detect when the pen leave
	 *           the sensing region of the tablet.
	 */
	public Emitter<Packet> getPacketsEmitter();
}
