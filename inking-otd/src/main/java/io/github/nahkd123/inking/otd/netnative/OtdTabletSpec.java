package io.github.nahkd123.inking.otd.netnative;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Optional;

import io.github.nahkd123.inking.api.tablet.ButtonType;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.Vector2;

public class OtdTabletSpec implements TabletInfo {
	private String tabletName;
	private int maxPressure;
	private Optional<Vector2> physicalSize;
	private Optional<Vector2> inputSize;
	private int penButtons;
	private int auxButtons;

	public OtdTabletSpec(MemorySegment memory) {
		tabletName = memory.get(OtdNative.stringLayout(), 0L).getUtf8String(0L);
		maxPressure = memory.get(ValueLayout.JAVA_INT, ValueLayout.ADDRESS.byteSize());
		float pW = memory.get(ValueLayout.JAVA_FLOAT, ValueLayout.ADDRESS.byteSize() + 4L);
		float pH = memory.get(ValueLayout.JAVA_FLOAT, ValueLayout.ADDRESS.byteSize() + 8L);
		float iW = memory.get(ValueLayout.JAVA_FLOAT, ValueLayout.ADDRESS.byteSize() + 12L);
		float iH = memory.get(ValueLayout.JAVA_FLOAT, ValueLayout.ADDRESS.byteSize() + 16L);
		physicalSize = Optional.of(new Vector2(pW, pH, MeasurementUnit.MILLIMETER));
		inputSize = Optional.of(new Vector2(iW, iH));
		penButtons = memory.get(ValueLayout.JAVA_INT, ValueLayout.ADDRESS.byteSize() + 20L);
		auxButtons = memory.get(ValueLayout.JAVA_INT, ValueLayout.ADDRESS.byteSize() + 24L);
	}

	@Override
	public String getTabletName() { return tabletName; }

	@Override
	public int getMaxPressure() { return maxPressure; }

	@Override
	public Optional<Vector2> getPhysicalSize() { return physicalSize; }

	@Override
	public Optional<Vector2> getInputSize() { return inputSize; }

	@Override
	public int getButtonsCount(ButtonType type) {
		return switch (type) {
		case PEN -> penButtons;
		case AUXILIARY -> auxButtons;
		default -> 0;
		};
	}

	public static MemoryLayout layout() {
		return MemoryLayout.structLayout(
			OtdNative.stringLayout().withName("tabletName"),
			ValueLayout.JAVA_INT.withName("maxPressure"),
			MemoryLayout.sequenceLayout(2L, ValueLayout.JAVA_FLOAT).withName("physicalSize"),
			MemoryLayout.sequenceLayout(2L, ValueLayout.JAVA_FLOAT).withName("inputSize"),
			ValueLayout.JAVA_INT.withName("penButtons"),
			ValueLayout.JAVA_INT.withName("auxButtons"));
	}
}
