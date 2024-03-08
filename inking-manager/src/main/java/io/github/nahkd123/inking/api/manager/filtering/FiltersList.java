package io.github.nahkd123.inking.api.manager.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.util.EmitterSource;

public class FiltersList implements FilterHost {
	private Consumer<Packet> consumer;
	private Entry head = null, tail = null;

	private class Entry implements FilterHost {
		private FiltersList list;
		private TabletFilter<?> filter;
		private Entry previous = null, next = null;

		public Entry(FiltersList list, TabletFilter<?> filter) {
			this.list = list;
			this.filter = filter;
		}

		@Override
		public void push(Tablet tablet, Packet packet) {
			if (next != null) next.filter.filterPacket(tablet, packet, next);
			else list.consumer.accept(packet);
		}
	}

	public FiltersList(Consumer<Packet> consumer) {
		this.consumer = consumer;
	}

	public FiltersList(EmitterSource<Packet> emitter) {
		this(emitter::push);
	}

	@Override
	public void push(Tablet tablet, Packet packet) {
		if (head != null) head.filter.filterPacket(tablet, packet, head);
		else consumer.accept(packet);
	}

	public void insertTail(TabletFilter<?> filter) {
		if (tail == null) {
			head = tail = new Entry(this, filter);
			return;
		}

		Entry e = new Entry(this, filter);
		e.previous = tail;
		tail.next = e;
		tail = e;
	}

	public void removeTail() {
		if (tail == null) return;
		Entry e = tail.previous;
		e.next = null;
		tail = e;
	}

	public void insertHead(TabletFilter<?> filter) {
		if (head == null) {
			head = tail = new Entry(this, filter);
			return;
		}

		Entry e = new Entry(this, filter);
		e.next = head;
		head.previous = e;
		head = e;
	}

	public void removeHead() {
		if (head == null) return;
		Entry e = head.next;
		e.previous = null;
		head = e;
	}

	public void removeAll() {
		head = tail = null;
	}

	public int indexOf(TabletFilter<?> filter) {
		int i = 0;
		Entry e = head;

		while (e != null) {
			if (e.filter == filter) return i;
			i++;
			e = e.next;
		}

		return -1;
	}

	public TabletFilter<?> getAt(int index) {
		if (index < 0) return null;

		int i = 0;
		Entry e = head;

		while (e != null) {
			if (i == index) return e.filter;
			i++;
			e = e.next;
		}

		return null;
	}

	public void insertAt(int index, TabletFilter<?> filter) {
		if (index < 0) return;

		if (index == 0) {
			insertHead(filter);
			return;
		}

		int i = 0;
		Entry e = head;

		while (e != null) {
			if (i == index) {
				Entry newEntry = new Entry(this, filter);
				Entry prev = e.previous;
				prev.next = newEntry;
				newEntry.previous = prev;
				newEntry.next = e;
				e.previous = newEntry;
				return;
			}

			i++;
			e = e.next;
		}

		insertTail(filter);
	}

	public List<TabletFilter<?>> getAsList() {
		List<TabletFilter<?>> filters = new ArrayList<>();
		Entry e = head;

		while (e != null) {
			filters.add(e.filter);
			e = e.next;
		}

		return Collections.unmodifiableList(filters);
	}

	public void setFromList(List<TabletFilter<?>> list) {
		removeAll();
		list.forEach(this::insertTail);
	}

	@Override
	public String toString() {
		String s = "filters {\n  device ->\n";
		Entry e = head;

		while (e != null) {
			s += "  " + e.filter.toString() + " ->\n";
			e = e.next;
		}

		return s + "  application\n}";
	}
}
