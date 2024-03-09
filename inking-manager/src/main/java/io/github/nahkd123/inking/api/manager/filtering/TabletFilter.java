package io.github.nahkd123.inking.api.manager.filtering;

import java.util.List;
import java.util.function.Consumer;

import io.github.nahkd123.inking.api.manager.config.Configurable;
import io.github.nahkd123.inking.api.tablet.Packet;

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
