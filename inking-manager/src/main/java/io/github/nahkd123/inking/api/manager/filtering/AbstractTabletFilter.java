package io.github.nahkd123.inking.api.manager.filtering;

import java.util.function.Consumer;

import io.github.nahkd123.inking.api.manager.filtering.host.FilterHost;
import io.github.nahkd123.inking.api.tablet.Packet;

public abstract class AbstractTabletFilter implements TabletFilter {
	private FilterHost host;
	private Consumer<Packet> receiver;

	@Override
	public void onInitialize(FilterHost host, Consumer<Packet> receiver) {
		this.host = host;
		this.receiver = receiver;
	}

	public FilterHost getHost() { return host; }

	public Consumer<Packet> getReceiver() { return receiver; }

	protected void push(Packet... packets) {
		for (Packet p : packets) receiver.accept(p);
	}
}
