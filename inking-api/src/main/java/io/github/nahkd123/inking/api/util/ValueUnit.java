package io.github.nahkd123.inking.api.util;

import java.text.DecimalFormat;
import java.text.ParseException;

public record ValueUnit(double value, MeasurementUnit unit) {
	public static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.##");
	public static final ValueUnit ZERO = new ValueUnit(0, MeasurementUnit.UNITLESS);

	public ValueUnit(double value) {
		this(value, MeasurementUnit.UNITLESS);
	}

	public ValueUnit(MeasurementUnit unit) {
		this(0, unit);
	}

	public static ValueUnit fromString(String s) {
		s = s.trim();

		try {
			for (MeasurementUnit unit : MeasurementUnit.values()) {
				for (String suffix : unit.getPossibleSuffixes()) {
					if (suffix.isEmpty()) continue;

					if (s.endsWith(suffix)) {
						s = s.substring(0, s.length() - suffix.length()).trim();
						return new ValueUnit(FORMATTER.parse(s).doubleValue());
					}
				}
			}

			return new ValueUnit(FORMATTER.parse(s).doubleValue());
		} catch (ParseException e) {
			throw new IllegalArgumentException("Failed to parse '" + s + "' measurement value: " + e.getMessage(), e);
		}
	}

	public ValueUnit withUnit(MeasurementUnit to) {
		if (unit == to) return this;
		if (unit == MeasurementUnit.UNITLESS) return new ValueUnit(value, to);

		if (unit.getBaseUnit() == to.getBaseUnit()) {
			double scale = unit.getBaseScale() / to.getBaseScale();
			return new ValueUnit(value * scale, to);
		}

		if (unit == MeasurementUnit.DEGREE && to == MeasurementUnit.RADIAN)
			return new ValueUnit(Math.toRadians(value), to);
		if (unit == MeasurementUnit.RADIAN && to == MeasurementUnit.DEGREE)
			return new ValueUnit(Math.toDegrees(value), to);

		throw new UnsupportedOperationException("Can't convert from " + unit.getFullName() + " to " + to.getFullName());
	}

	@Override
	public final String toString() {
		return FORMATTER.format(value) + unit.getUnitName();
	}
}
