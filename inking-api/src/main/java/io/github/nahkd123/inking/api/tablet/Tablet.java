package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.TabletDriver;
import io.github.nahkd123.inking.api.util.Emitter;

public interface Tablet {
	/**
	 * <p>
	 * Get the tablet driver that is driving this device.
	 * </p>
	 * 
	 * @return The driver.
	 */
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
	 * Get the information of this tablet.
	 * </p>
	 * 
	 * @return The info of this tablet.
	 */
	public TabletInfo getInfo();

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
