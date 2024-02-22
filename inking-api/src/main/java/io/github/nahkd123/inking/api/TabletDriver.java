package io.github.nahkd123.inking.api;

import java.util.Collection;

import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.util.Emitter;

/**
 * <p>
 * An interface for all tablet drivers. You can get all connected tablets, or
 * listen for when a new tablet is discovered, a tablet connected to or
 * disconnected from driver. To listen for tablet inputs, use
 * {@link Tablet#getPacketsEmitter()}.
 * </p>
 * 
 * @see #getTabletDiscoverEmitter()
 * @see #getTabletConnectEmitter()
 * @see #getTabletDisconnectEmitter()
 * @see Tablet
 */
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
	 * @see #getTabletDisconnectEmitter()
	 * @see #getTabletDiscoverEmitter()
	 */
	public Emitter<Tablet> getTabletConnectEmitter();

	/**
	 * <p>
	 * Get the emitter that emits when a tablet is disconnected.
	 * </p>
	 * 
	 * @return Emitter.
	 * @see #getTabletDisconnectEmitter()
	 */
	public Emitter<Tablet> getTabletDisconnectEmitter();

	/**
	 * <p>
	 * Get the emitter that emits when a new tablet is discovered by this driver. A
	 * tablet is considered to be discovered when it's unique identifier hasn't been
	 * seen before by the driver. Even if the tablet is disconnected, the tablet
	 * emitted from this emitter can still be used.
	 * </p>
	 * <p>
	 * This will only emits when an undiscovered tablet connected to the computer.
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
