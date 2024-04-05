package io.github.nahkd123.inking.api.manager.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

/**
 * <p>
 * Mapping graph maps the input values to a curve. Mainly for pen pressure
 * mapping.
 * </p>
 */
public class MappingGraph {
	private static final Dot ZERO = new Dot(0, 0);
	private List<Dot> dots = new ArrayList<>();

	private static final Codec<Dot> DOT_CODEC = Codec.INT.listOf().comapFlatMap(
		list -> {
			if (list.size() != 2) return DataResult.error(() -> "The graph point must be a list of 2 elements");
			return DataResult.success(new Dot(list.get(0), list.get(1)));
		},
		dot -> List.of(dot.x, dot.y));

	public static final Codec<MappingGraph> CODEC = DOT_CODEC.listOf().xmap(
		list -> {
			MappingGraph graph = new MappingGraph();
			list.forEach(l -> graph.add(l.x, l.y));
			return graph;
		},
		MappingGraph::getDots);

	public static class Dot implements Comparable<Dot> {
		private int x;
		private int y;
		private MappingGraph graph = null;

		private Dot(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() { return x; }

		public void setX(int x) {
			this.x = x;
			if (graph != null) Collections.sort(graph.dots);
		}

		public int getY() { return y; }

		public void setY(int y) { this.y = y; }

		@Override
		public int compareTo(Dot o) {
			return Double.compare(x, o.x);
		}

		public int map(Dot previous, int x) {
			if (x < previous.x) return previous.y;
			if (x > this.x) return this.y;
			int dx = x - previous.x;
			int spanX = this.x - previous.x;
			int spanY = this.y - previous.y;
			return previous.y + spanY * dx / spanX;
		}

		@Override
		public String toString() {
			return "(" + x + " => " + y + ")";
		}
	}

	/**
	 * <p>
	 * Add a new point.
	 * </p>
	 * 
	 * @param x The input value.
	 * @param y The desired output value for given input value.
	 * @return The dot, which can be used for changing the X, Y or exponent value if
	 *         you wanted to.
	 */
	public Dot add(int x, int y) {
		Dot dot = new Dot(x, y);
		dot.graph = this;
		dots.add(dot);
		Collections.sort(dots);
		return dot;
	}

	public List<Dot> getDots() { return Collections.unmodifiableList(dots); }

	public boolean remove(Dot dot) {
		if (dot.graph != this) return false;
		dot.graph = null;
		return dots.remove(dot);
	}

	public void clearAll() {
		dots.forEach(d -> d.graph = null);
		dots.clear();
	}

	public int map(int x, int max) {
		int search = Collections.binarySearch(dots, new Dot(x, 0));
		if (search >= 0) return dots.get(search).y;

		int insert = -search - 1;
		Dot prev = insert > 0 ? dots.get(insert - 1) : ZERO;
		Dot next = insert < dots.size()
			? dots.get(insert)
			: prev == ZERO ? new Dot(max, max) : prev;
		return next.map(prev, x);
	}
}
