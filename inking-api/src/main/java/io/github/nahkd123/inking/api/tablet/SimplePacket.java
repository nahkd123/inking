package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.util.Flags;
import io.github.nahkd123.inking.api.util.Vector2;

public record SimplePacket(Vector2 penPosition, int rawPressure, Flags states, long penButtons, long auxButtons, long timestamp) implements Packet {
	public SimplePacket(Vector2 penPosition, int rawPressure, long flags, long penButtons, long auxButtons, long timestamp) {
		this(penPosition, rawPressure, new Flags(flags), penButtons, auxButtons, timestamp);
	}

	@Override
	public Vector2 getPenPosition() { return penPosition; }

	@Override
	public int getRawPressure() { return rawPressure; }

	@Override
	public Flags getPenStates() { return states; }

	@Override
	public long getTimestamp() { return timestamp; }

	@Override
	public boolean isButtonDown(ButtonType type, int index) {
		long set = type == ButtonType.PEN ? penButtons : type == ButtonType.AUXILIARY ? auxButtons : 0L;
		return (set & (1L << index)) != 0;
	}
}
