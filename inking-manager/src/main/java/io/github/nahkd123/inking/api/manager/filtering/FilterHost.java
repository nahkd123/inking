package io.github.nahkd123.inking.api.manager.filtering;

import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.Tablet;

public interface FilterHost {
	/**
	 * <p>
	 * Push the packet to the next filter in the filters chain, or push to the
	 * application if the current filter is the last one.
	 * </p>
	 * 
	 * @param tablet The tablet where the original packet is originated from. Can be
	 *               changed to different tablet, but that would confuse other
	 *               filters
	 * @param packet The packet to push.
	 */
	public void push(Tablet tablet, Packet packet);
}
