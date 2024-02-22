package io.github.nahkd123.inking.api.util;

public interface Vector2 {
	public double x();

	public double y();

	default ConstantVector2 add(Vector2 another) {
		return new ConstantVector2(x() + another.x(), y() + another.y());
	}

	default ConstantVector2 subtract(Vector2 another) {
		return new ConstantVector2(x() - another.x(), y() - another.y());
	}
}
