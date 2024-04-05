package io.github.nahkd123.inking.api.util;

public enum MeasurementUnit {
	UNITLESS("Unitless", "", null, 1),
	PIXEL("Pixel", "px", null, 1),
	METER("Meter", "m", null, 1),
	MILLIMETER("Millimeter", "mm", METER, 1d / 1000d),
	CENTIMETER("Centimeter", "cm", METER, 1d / 100d),
	RADIAN("Radian", "rad", null, 1),
	DEGREE("Degree", "deg", null, 1);

	private String fullName;
	private String unitName;
	private String[] possibleSuffixes;
	private MeasurementUnit baseUnit;
	private double baseScale;

	private MeasurementUnit(String fullName, String unitName, MeasurementUnit baseUnit, double baseScale) {
		this.fullName = fullName;
		this.unitName = unitName;
		this.possibleSuffixes = new String[] { fullName, fullName.toLowerCase(), unitName, unitName.toUpperCase() };
		this.baseUnit = baseUnit;
		this.baseScale = baseScale;
	}

	public String getFullName() { return fullName; }

	public String getUnitName() { return unitName; }

	public String[] getPossibleSuffixes() { return possibleSuffixes; }

	public MeasurementUnit getBaseUnit() { return baseUnit != null ? baseUnit : this; }

	public double getBaseScale() { return baseScale; }
}
