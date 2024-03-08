package io.github.nahkd123.inking.api.manager.filtering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.Tablet;

class FiltersListTest {
	@SuppressWarnings("rawtypes")
	@Test
	void test() {
		AtomicInteger applicationCounter = new AtomicInteger(0);
		AtomicInteger filterCounter = new AtomicInteger(0);
		FiltersList filters = new FiltersList(p -> applicationCounter.addAndGet(1));
		TabletFilter a, b, c;

		filters.insertTail(a = new TabletFilter() {
			@Override
			public FilterFactory getFactory() { return null; }

			@Override
			public void filterPacket(Tablet tablet, Packet packet, FilterHost host) {
				filterCounter.addAndGet(1);
				host.push(tablet, packet);
			}
		});
		filters.insertTail(b = new TabletFilter() {
			@Override
			public FilterFactory getFactory() { return null; }

			@Override
			public void filterPacket(Tablet tablet, Packet packet, FilterHost host) {
				filterCounter.addAndGet(1);
				host.push(tablet, packet);
				host.push(tablet, packet);
			}
		});
		filters.insertTail(c = new TabletFilter() {
			@Override
			public FilterFactory getFactory() { return null; }

			@Override
			public void filterPacket(Tablet tablet, Packet packet, FilterHost host) {
				filterCounter.addAndGet(1);
				host.push(tablet, packet);
				host.push(tablet, packet);
				host.push(tablet, packet);
			}
		});

		filters.push(null, null);
		assertEquals(6, applicationCounter.get());
		assertEquals(4, filterCounter.get());

		filters.removeTail();
		filters.push(null, null);
		assertEquals(6 + 2, applicationCounter.get());
		assertEquals(4 + 2, filterCounter.get());
		assertEquals(2, filters.getAsList().size());
		assertEquals(-1, filters.indexOf(c));
		assertEquals(1, filters.indexOf(b));

		filters.insertHead(c);
		assertEquals(0, filters.indexOf(c));
		assertEquals(1, filters.indexOf(a));
		assertEquals(2, filters.indexOf(b));
		assertEquals(c, filters.getAt(0));
		assertEquals(a, filters.getAt(1));
		assertEquals(b, filters.getAt(2));

		filters.insertAt(1, new TabletFilter() {
			@Override
			public FilterFactory getFactory() { return null; }
		});
		assertEquals(0, filters.indexOf(c));
		assertEquals(2, filters.indexOf(a));
		assertEquals(3, filters.indexOf(b));
	}
}
