package io.github.nahkd123.inking.api.util;

public record ConstantVector2(double x, double y) implements Vector2 {
	public static final ConstantVector2 ZERO = new ConstantVector2(0, 0);

	@Override
	public String toString() {
		return "(" + x + "; " + y + ")";
	}
}
