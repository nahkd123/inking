package io.github.nahkd123.inking.api.manager.filtering;

import java.util.List;

import io.github.nahkd123.inking.api.manager.config.Configurable;
import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.Tablet;

public interface TabletFilter<T extends TabletFilter<T>> {
	public FilterFactory<T> getFactory();

	/**
	 * <p>
	 * Filter the incoming packet. The packet can be either directly from tablet or
	 * from other filters in the filters chain. Default behavior for this is
	 * passthrough (send the received packet instantly to
	 * {@link FilterHost#push(Packet)}.
	 * </p>
	 * 
	 * @param tablet The tablet that created the first packet in the filters chain.
	 * @param packet The received packet.
	 * @param host   The host that is hosting this tablet filter. You can use this
	 *               to push packets.
	 */
	default void filterPacket(Tablet tablet, Packet packet, FilterHost host) {
		host.push(tablet, packet);
	}

	@SuppressWarnings("unchecked")
	default List<Configurable> getConfig() { return getFactory().getFilterConfig((T) this); }
}
