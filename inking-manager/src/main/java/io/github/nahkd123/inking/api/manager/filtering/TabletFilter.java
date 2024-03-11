package io.github.nahkd123.inking.api.manager.filtering;

import java.util.List;
import java.util.function.Consumer;

import io.github.nahkd123.inking.api.manager.config.Configurable;
import io.github.nahkd123.inking.api.tablet.MutablePacket;
import io.github.nahkd123.inking.api.tablet.Packet;

/**
 * <p>
 * Tablet filters allows user to apply effects/filters to the tablet's packets
 * stream. The filter can cancel the packet, modify the packet or generate new
 * packets based on historical data.
 * </p>
 * <p>
 * <b>Modifications to a single packet</b>: Use
 * {@link MutablePacket#mutableOf(Packet)} for better memory usage. This will
 * returns a new {@link MutablePacket} if the packet is not mutable, or returns
 * the value from parameter if it is mutable.
 * </p>
 * 
 * @param <T> The filter type.
 */
public interface TabletFilter<T extends TabletFilter<T>> {
	public FilterFactory<T> getFactory();

	/**
	 * <p>
	 * Filter the incoming packet. The packet can be either directly from tablet or
	 * from other filters in the filters chain. Default behavior for this is
	 * passthrough (send the received packet instantly to pusher.
	 * </p>
	 * 
	 * @param packet The received packet.
	 * @param pusher The packets pusher. Pushing packets through this pusher will
	 *               either send the packet to the next filter in the chain, or send
	 *               it directly to application.
	 * @param host   The host that is hosting this tablet filter. You can obtain
	 *               various information about the driver host, like application
	 *               window size for example.
	 */
	default void filterPacket(Packet packet, Consumer<Packet> pusher, FilterHost host) {
		pusher.accept(packet);
	}

	@SuppressWarnings("unchecked")
	default List<Configurable> getConfig() { return getFactory().getFilterConfig((T) this); }
}
