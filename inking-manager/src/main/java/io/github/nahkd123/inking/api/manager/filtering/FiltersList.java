package io.github.nahkd123.inking.api.manager.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.github.nahkd123.inking.api.tablet.Packet;

public class FiltersList {
	private Entry head = null, tail = null;

	private class Entry {
		private TabletFilter<?> filter;
		private Entry previous = null, next = null;

		public Entry(TabletFilter<?> filter) {
			this.filter = filter;
		}

		public void filter(Packet packet, FilterHost host, Consumer<Packet> consumer) {
			Consumer<Packet> pusher = next != null
				? p -> next.filter.filterPacket(p, filtered -> next.filter(filtered, host, consumer), host)
				: consumer;
			pusher.accept(packet);
		}
	}

	public void filter(Packet packet, FilterHost host, Consumer<Packet> consumer) {
		Consumer<Packet> pusher = head != null
			? p -> head.filter.filterPacket(p, filtered -> head.filter(filtered, host, consumer), host)
			: consumer;
		pusher.accept(packet);
	}

	public void insertTail(TabletFilter<?> filter) {
		if (tail == null) {
			head = tail = new Entry(filter);
			return;
		}

		Entry e = new Entry(filter);
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
			head = tail = new Entry(filter);
			return;
		}

		Entry e = new Entry(filter);
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
				Entry newEntry = new Entry(filter);
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
