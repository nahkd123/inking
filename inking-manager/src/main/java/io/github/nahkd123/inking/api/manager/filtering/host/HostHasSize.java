package io.github.nahkd123.inking.api.manager.filtering.host;

import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.Vector2;

/**
 * <p>
 * Reports the window/screen size from the host.
 * </p>
 */
public interface HostHasSize extends FilterHost {
	/**
	 * <p>
	 * Get the size reported from the host. Filters may use this to map
	 * {@link Packet#getPenPosition()} to a position on the rectangle with
	 * {@link #getHostSize()} as size.
	 * </p>
	 * <p>
	 * In some rare cases, the reported measurement unit can be something that isn't
	 * {@link MeasurementUnit#PIXEL}.
	 * </p>
	 * 
	 * @return The size of the host.
	 */
	public Vector2 getHostSize();
}
