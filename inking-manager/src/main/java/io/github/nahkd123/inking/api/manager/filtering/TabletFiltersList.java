package io.github.nahkd123.inking.api.manager.filtering;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;

import io.github.nahkd123.inking.api.manager.filtering.host.FilterHost;
import io.github.nahkd123.inking.api.tablet.Packet;

/**
 * <p>
 * A list of packet filters. Any changes to the filters list while this list is
 * initialized with filter host will cause all existing filters to reinitialize.
 * </p>
 * <p>
 * Initializing with {@link #initialize(FilterHost, Consumer)} and finalize (or
 * detach from application) with {@link #detach()}. Once detached, any inputs
 * from device will be ignored.
 * </p>
 * <p>
 * To send inputs, call {@link #pushPackets(Packet...)} with your own packets.
 * This method is also called by input thread.
 * </p>
 * 
 * @see #initialize(FilterHost, Consumer)
 * @see #detach()
 * @see #pushPackets(Packet...)
 */
public class TabletFiltersList extends AbstractList<TabletFilter> {
	private List<TabletFilter> filters = new ArrayList<>();
	private FilterHost host;
	private Consumer<Packet> receiver;

	public TabletFiltersList(List<TabletFilter> filters) {
		this.filters.addAll(filters);
	}

	public TabletFiltersList() {}

	public static final Codec<TabletFiltersList> CODEC = TabletFilter.CODEC.listOf().xmap(
		TabletFiltersList::new,
		list -> list);

	@Override
	public int size() {
		return filters.size();
	}

	@Override
	public TabletFilter get(int index) {
		return filters.get(index);
	}

	@Override
	public void add(int index, TabletFilter element) {
		filters.add(index, element);
		if (host != null) initialize(host, receiver);
	}

	@Override
	public boolean addAll(int index, Collection<? extends TabletFilter> c) {
		boolean changed = filters.addAll(index, c);
		if (changed && host != null) initialize(host, receiver);
		return changed;
	}

	@Override
	public TabletFilter remove(int index) {
		TabletFilter filter = filters.remove(index);
		if (filter != null && host != null) initialize(host, receiver);
		return filter;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = filters.removeAll(c);
		if (changed && host != null) initialize(host, receiver);
		return changed;
	}

	@Override
	public boolean removeIf(Predicate<? super TabletFilter> filter) {
		boolean changed = filters.removeIf(filter);
		if (changed && host != null) initialize(host, receiver);
		return changed;
	}

	/**
	 * <p>
	 * Initialize this filters list with new host and packets receiver. Please note
	 * that the reciever is processed in device's input thread.
	 * </p>
	 * <p>
	 * This method is also called when add/remove operations invoked.
	 * </p>
	 * 
	 * @param host     The host.
	 * @param receiver The packets receiver.
	 */
	public void initialize(FilterHost host, Consumer<Packet> receiver) {
		this.host = host;
		this.receiver = receiver;

		Consumer<Packet> proxiedReceiver = packet -> {
			if (this.receiver != null) this.receiver.accept(packet);
		};

		for (int i = 0; i < size(); i++) {
			TabletFilter filter = filters.get(i);
			Consumer<Packet> targetReceiver = i < size() - 1 ? filters.get(i)::onPacket : proxiedReceiver;
			filter.onInitialize(host, targetReceiver);
		}
	}

	public boolean isAttached() { return host != null; }

	/**
	 * <p>
	 * Detach this filters list from application, preventing inputs from device to
	 * send to application that was previously attached to this filters list from
	 * {@link #initialize(FilterHost, Consumer)}.
	 * </p>
	 * 
	 * @return {@code true} if attached successfully.
	 */
	public boolean detach() {
		if (host == null) return false;
		host = null;
		receiver = null;
		return true;
	}

	/**
	 * <p>
	 * Push packets to all filters, then from filters to application's packets
	 * receiver. Do nothing if no receivers attached.
	 * </p>
	 * <p>
	 * This method should be called in device's input thread.
	 * </p>
	 * 
	 * @param packets An array of packets to push for processing.
	 */
	public void pushPackets(Packet... packets) {
		if (host == null) return;

		for (Packet p : packets) {
			if (filters.size() > 0) filters.get(0).onPacket(p);
			else receiver.accept(p);
		}
	}
}
