package io.github.nahkd123.inking.api.manager.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Mapping graph maps the input values to a curve. Mainly for pen pressure
 * mapping.
 * </p>
 */
public class MappingGraph {
	private static final Dot ZERO = new Dot(0, 0, 1);
	private List<Dot> dots = new ArrayList<>();

	public static class Dot implements Comparable<Dot> {
		private double x;
		private double y;
		private double exp;
		private MappingGraph graph = null;

		private Dot(double x, double y, double exp) {
			this.x = x;
			this.y = y;
			this.exp = exp;
		}

		public double getX() { return x; }

		public void setX(double x) {
			this.x = x;
			if (graph != null) Collections.sort(graph.dots);
		}

		public double getY() { return y; }

		public void setY(double y) { this.y = y; }

		public double getExp() { return exp; }

		public void setExp(double exp) { this.exp = exp; }

		@Override
		public int compareTo(Dot o) {
			return Double.compare(x, o.x);
		}

		public double map(Dot previous, double x) {
			if (x < previous.x) return previous.y;
			if (x > this.x) return this.y;
			double dx = x - previous.x;
			double spanX = this.x - previous.y;
			double spanY = this.y - previous.y;
			return previous.y + spanY * Math.pow(dx / spanX, exp);
		}

		@Override
		public String toString() {
			return "(" + x + " [" + exp + "]=> " + y + ")";
		}
	}

	/**
	 * <p>
	 * Add a new point.
	 * </p>
	 * 
	 * @param x   The input value.
	 * @param y   The desired output value for given input value.
	 * @param exp The exponent value.
	 * @return The dot, which can be used for changing the X, Y or exponent value if
	 *         you wanted to.
	 */
	public Dot add(double x, double y, double exp) {
		Dot dot = new Dot(x, y, exp);
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

	public double map(double x, double max) {
		int search = Collections.binarySearch(dots, new Dot(x, 0, 1));
		if (search >= 0) return dots.get(search).y;

		int insert = -search - 1;
		Dot prev = insert > 0 ? dots.get(insert - 1) : ZERO;
		Dot next = insert < dots.size()
			? dots.get(insert)
			: prev == ZERO ? new Dot(max, max, 1) : prev;
		return next.map(prev, x);
	}
}
