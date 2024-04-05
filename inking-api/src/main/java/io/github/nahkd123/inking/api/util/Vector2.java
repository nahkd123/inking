package io.github.nahkd123.inking.api.util;

import java.text.DecimalFormat;

/**
 * <p>
 * A point in 2D plane, a size of 2D rectangle or a pair of 2 angles. All
 * vectors of this record type have {@link MeasurementUnit} attached.
 * </p>
 */
public record Vector2(double x, double y, MeasurementUnit unit) {

	public static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.##");
	public static final Vector2 PHYSICAL_ZERO = new Vector2(MeasurementUnit.MILLIMETER);
	public static final Vector2 ANGLE_ZERO = new Vector2(MeasurementUnit.RADIAN);
	public static final Vector2 ZERO = new Vector2(MeasurementUnit.UNITLESS);

	public Vector2(double x, double y) {
		this(x, y, MeasurementUnit.UNITLESS);
	}

	public Vector2(MeasurementUnit unit) {
		this(0, 0, unit);
	}

	public static Vector2 from(ValueUnit x, ValueUnit y) {
		if (x.unit() != y.unit())
			throw new IllegalArgumentException("Measurement unit mismatch: " + x.unit() + " !=" + y.unit());
		return new Vector2(x.value(), y.value(), x.unit());
	}

	public Vector2 withUnit(MeasurementUnit to) {
		if (unit == to) return this;
		if (unit == MeasurementUnit.UNITLESS) return new Vector2(x, y, to);

		if (unit.getBaseUnit() == to.getBaseUnit()) {
			double scale = unit.getBaseScale() / to.getBaseScale();
			return new Vector2(x * scale, y * scale, to);
		}

		if (unit == MeasurementUnit.DEGREE && to == MeasurementUnit.RADIAN)
			return new Vector2(Math.toRadians(x), Math.toRadians(y), to);
		if (unit == MeasurementUnit.RADIAN && to == MeasurementUnit.DEGREE)
			return new Vector2(Math.toDegrees(x), Math.toDegrees(y), to);

		throw new UnsupportedOperationException("Can't convert from " + unit.getFullName() + " to " + to.getFullName());
	}

	public ValueUnit xUnit() {
		return new ValueUnit(x, unit);
	}

	public ValueUnit yUnit() {
		return new ValueUnit(y, unit);
	}

	@Override
	public String toString() {
		return "(" + FORMATTER.format(x) + unit.getUnitName() + "; " + FORMATTER.format(y) + unit.getUnitName() + ")";
	}
}
