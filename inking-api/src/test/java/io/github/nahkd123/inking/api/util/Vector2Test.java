package io.github.nahkd123.inking.api.util;

import org.junit.jupiter.api.Test;

class Vector2Test {
	@Test
	void test() {
		new Vector2(5, 10, MeasurementUnit.CENTIMETER).withUnit(MeasurementUnit.MILLIMETER);
	}
}
