package io.github.nahkd123.inking.api.manager.filtering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import io.github.nahkd123.inking.api.tablet.Packet;

class FiltersListTest {
	@FunctionalInterface
	interface Filterer {
		void filterPacket(Packet packet, Consumer<Packet> pusher, FilterHost host);
	}

	class SimpleFilterFactory implements FilterFactory<SimpleFilter> {
		private Filterer filter;

		public SimpleFilterFactory(Filterer filter) {
			this.filter = filter;
		}

		@Override
		public SimpleFilter createDefaultFilter() {
			return new SimpleFilter(this);
		}
	}

	class SimpleFilter implements TabletFilter<SimpleFilter> {
		private SimpleFilterFactory factory;

		public SimpleFilter(SimpleFilterFactory factory) {
			this.factory = factory;
		}

		@Override
		public FilterFactory<SimpleFilter> getFactory() { return factory; }

		@Override
		public void filterPacket(Packet packet, Consumer<Packet> pusher, FilterHost host) {
			factory.filter.filterPacket(packet, pusher, host);
		}
	}

	@Test
	void test() {
		AtomicInteger applicationCounter = new AtomicInteger(0);
		AtomicInteger filterCounter = new AtomicInteger(0);
		FiltersList filters = new FiltersList();
		SimpleFilter a, b, c;

		filters.insertTail(a = new SimpleFilterFactory((packet, pusher, host) -> {
			filterCounter.addAndGet(1);
			pusher.accept(packet);
		}).createDefaultFilter());
		filters.insertTail(b = new SimpleFilterFactory((packet, pusher, host) -> {
			filterCounter.addAndGet(1);
			pusher.accept(packet);
			pusher.accept(packet);
		}).createDefaultFilter());
		filters.insertTail(c = new SimpleFilterFactory((packet, pusher, host) -> {
			filterCounter.addAndGet(1);
			pusher.accept(packet);
			pusher.accept(packet);
			pusher.accept(packet);
		}).createDefaultFilter());

		filters.filter(null, null, p -> applicationCounter.addAndGet(1));
		assertEquals(6, applicationCounter.get());
		assertEquals(4, filterCounter.get());

		filters.removeTail();
		filters.filter(null, null, p -> applicationCounter.addAndGet(1));
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

		filters.insertAt(1, new SimpleFilterFactory((packet, pusher, host) -> {}).createDefaultFilter());
		assertEquals(0, filters.indexOf(c));
		assertEquals(2, filters.indexOf(a));
		assertEquals(3, filters.indexOf(b));
	}
}
