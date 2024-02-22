package io.github.nahkd123.inking.api;

import java.util.Collection;

import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.util.Emitter;

public interface TabletDriver {
	/**
	 * <p>
	 * Get the display name of this tablet driver. For example: "OpenTabletDriver"
	 * or "Windows Ink".
	 * </p>
	 * 
	 * @return The display name of this driver.
	 */
	public String getDriverName();

	/**
	 * <p>
	 * Get the emitter that emits when a tablet is connected.
	 * </p>
	 * 
	 * @return Emitter.
	 */
	public Emitter<Tablet> getTabletConnectEmitter();

	/**
	 * <p>
	 * Get the emitter that emits when a tablet is disconnected.
	 * </p>
	 * 
	 * @return Emitter.
	 */
	public Emitter<Tablet> getTabletDisconnectEmitter();

	/**
	 * <p>
	 * Get the emitter that emits when a new tablet is discovered by this driver.
	 * </p>
	 * 
	 * @return Emitter.
	 */
	public Emitter<Tablet> getTabletDiscoverEmitter();

	/**
	 * <p>
	 * Get a collection of tablets that is being connected to this driver.
	 * </p>
	 * 
	 * @return Collection of connected tablets.
	 */
	public Collection<Tablet> getConnectedTablets();
}
