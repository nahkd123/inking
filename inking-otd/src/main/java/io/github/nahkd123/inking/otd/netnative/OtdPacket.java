package io.github.nahkd123.inking.otd.netnative;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import io.github.nahkd123.inking.api.tablet.ButtonType;
import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.util.Flags;
import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.Vector2;

public class OtdPacket implements Packet {
	private long timestamp;
	private Flags states;
	private Vector2 position;
	private Vector2 tilt;
	private int pressure;
	private int hoverDistance;
	private long penButtons;
	private long auxButtons;

	/**
	 * <p>
	 * Copy {@link MemorySegment} data into a new packet, allowing the segment to be
	 * freed in arena.
	 * </p>
	 * 
	 * @param memory The memory segment.
	 */
	public OtdPacket(MemorySegment memory) {
		states = new Flags(memory.get(ValueLayout.JAVA_LONG, 0L));
		position = new Vector2(memory.get(ValueLayout.JAVA_FLOAT, 8L), memory.get(ValueLayout.JAVA_FLOAT, 12L));
		tilt = new Vector2(memory.get(ValueLayout.JAVA_FLOAT, 16L), memory.get(ValueLayout.JAVA_FLOAT,
			20L), MeasurementUnit.DEGREE);
		pressure = memory.get(ValueLayout.JAVA_INT, 24L);
		hoverDistance = memory.get(ValueLayout.JAVA_INT, 28L);
		penButtons = memory.get(ValueLayout.JAVA_INT, 32L);
		auxButtons = memory.get(ValueLayout.JAVA_INT, 40L);
		timestamp = memory.get(ValueLayout.JAVA_LONG, 48L);
	}

	@Override
	public long getTimestamp() { return timestamp; }

	@Override
	public Vector2 getPenPosition() { return position; }

	@Override
	public Vector2 getTilt() { return tilt; }

	@Override
	public int getRawPressure() { return pressure; }

	@Override
	public int getRawHoverDistance() { return hoverDistance; }

	@Override
	public Flags getPenStates() { return states; }

	@Override
	public long getButtonsDown(ButtonType type) {
		return switch (type) {
		case PEN -> penButtons;
		case AUXILIARY -> auxButtons;
		default -> 0L;
		};
	}

	public static MemoryLayout layout() {
		return MemoryLayout.structLayout(
			ValueLayout.JAVA_LONG.withName("states"),
			MemoryLayout.sequenceLayout(2L, ValueLayout.JAVA_FLOAT).withName("position"),
			MemoryLayout.sequenceLayout(2L, ValueLayout.JAVA_FLOAT).withName("tilt"),
			ValueLayout.JAVA_INT.withName("pressure"),
			ValueLayout.JAVA_INT.withName("hoverDistance"),
			ValueLayout.JAVA_LONG.withName("penButtons"),
			ValueLayout.JAVA_LONG.withName("auxButtons"),
			ValueLayout.JAVA_LONG.withName("timestamp"));
	}
}
