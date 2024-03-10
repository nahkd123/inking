package io.github.nahkd123.inking.api.manager.utils;

import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.Vector2;

public record Rectangle(double x, double y, double width, double height, MeasurementUnit unit) {
	public Rectangle fromVectors(Vector2 origin, Vector2 size) {
		if (origin.unit() != size.unit())
			throw new IllegalArgumentException("Both origin and size must have the same measurement unit");
		return new Rectangle(origin.x(), origin.y(), size.x(), size.y(), origin.unit());
	}

	public Vector2 origin() {
		return new Vector2(x, y, unit);
	}

	public Vector2 size() {
		return new Vector2(width, height, unit);
	}

	@Override
	public String toString() {
		return "(" + origin() + "; " + size() + ")";
	}
}
