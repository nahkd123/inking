package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.util.Flags;
import io.github.nahkd123.inking.api.util.Vector2;

public record ImmutablePacket(Vector2 penPosition, Vector2 penTilt, int rawPressure, int hoveringDistance, Flags states, long penButtons, long auxButtons, long timestamp) implements Packet {
	public ImmutablePacket(Vector2 penPosition, Vector2 penTilt, int rawPressure, int hoveringDistance, long flags, long penButtons, long auxButtons, long timestamp) {
		this(penPosition, penTilt, rawPressure, hoveringDistance, new Flags(flags), penButtons, auxButtons, timestamp);
	}

	public static ImmutablePacket copyFrom(Packet packet) {
		Vector2 penPosition = packet.getPenPosition();
		Vector2 penTilt = packet.getTilt();
		int rawPressure = packet.getRawPressure();
		int hoveringDistance = packet.getRawHoverDistance();
		Flags states = packet.getPenStates();
		long penButtons = packet.getButtonsDown(ButtonType.PEN);
		long auxButtons = packet.getButtonsDown(ButtonType.AUXILIARY);
		long timestamp = packet.getTimestamp();
		return new ImmutablePacket(penPosition, penTilt, rawPressure, hoveringDistance, states, penButtons, auxButtons, timestamp);
	}

	@Override
	public Vector2 getPenPosition() { return penPosition; }

	@Override
	public Vector2 getTilt() { return penTilt; }

	@Override
	public int getRawPressure() { return rawPressure; }

	@Override
	public int getRawHoverDistance() { return hoveringDistance; }

	@Override
	public Flags getPenStates() { return states; }

	@Override
	public long getTimestamp() { return timestamp; }

	@Override
	public long getButtonsDown(ButtonType type) {
		return type == ButtonType.PEN ? penButtons : type == ButtonType.AUXILIARY ? auxButtons : 0L;
	}
}
