package io.github.nahkd123.inking.api.tablet;

import io.github.nahkd123.inking.api.util.Flags;
import io.github.nahkd123.inking.api.util.Vector2;

/**
 * <p>
 * A mutable version of {@link Packet}. Should be used instead of
 * {@link ImmutablePacket} in filters to avoid excessive allocations. See
 * {@link #mutableOf(Packet)} for more info.
 * </p>
 */
public class MutablePacket implements Packet {
	private Vector2 penPosition;
	private Vector2 penTilt;
	private int rawPressure;
	private int hoveringDistance;
	private Flags states;
	private long penButtons;
	private long auxButtons;
	private long timestamp;

	public MutablePacket(Vector2 penPosition, Vector2 penTilt, int rawPressure, int hoveringDistance, Flags states, long penButtons, long auxButtons, long timestamp) {
		this.penPosition = penPosition;
		this.penTilt = penTilt;
		this.rawPressure = rawPressure;
		this.hoveringDistance = hoveringDistance;
		this.states = states;
		this.penButtons = penButtons;
		this.auxButtons = auxButtons;
		this.timestamp = timestamp;
	}

	/**
	 * <p>
	 * Get the mutable version of packet. This method will returns the value
	 * provided from the parameter if the parameter is {@link MutablePacket},
	 * otherwise all properties will be copied to a new {@link MutablePacket}.
	 * </p>
	 * <p>
	 * Mutable packets should be used in filters to minimize memory usage, because a
	 * professional graphics tablet can emit up to 1000 packets per second (1000Hz).
	 * </p>
	 * 
	 * @param packet The packet.
	 * @return Either the input packet (if packet is {@link MutablePacket} already)
	 *         or a copy of packet as {@link MutablePacket}.
	 */
	public static MutablePacket mutableOf(Packet packet) {
		if (packet instanceof MutablePacket mutable) return mutable;
		Vector2 penPosition = packet.getPenPosition();
		Vector2 penTilt = packet.getTilt();
		int rawPressure = packet.getRawPressure();
		int hoveringDistance = packet.getRawHoverDistance();
		Flags states = packet.getPenStates();
		long penButtons = packet.getButtonsDown(ButtonType.PEN);
		long auxButtons = packet.getButtonsDown(ButtonType.AUXILIARY);
		long timestamp = packet.getTimestamp();
		return new MutablePacket(penPosition, penTilt, rawPressure, hoveringDistance, states, penButtons, auxButtons, timestamp);
	}

	@Override
	public Vector2 getPenPosition() { return penPosition; }

	public void setPenPosition(Vector2 penPosition) { this.penPosition = penPosition; }

	@Override
	public Vector2 getTilt() { return penTilt; }

	public void setTilt(Vector2 penTilt) { this.penTilt = penTilt; }

	@Override
	public int getRawPressure() { return rawPressure; }

	public void setRawPressure(int rawPressure) { this.rawPressure = rawPressure; }

	@Override
	public int getRawHoverDistance() { return hoveringDistance; }

	public void setRawHoverDistance(int hoveringDistance) { this.hoveringDistance = hoveringDistance; }

	@Override
	public Flags getPenStates() { return states; }

	public void setPenStates(Flags states) { this.states = states; }

	@Override
	public long getTimestamp() { return timestamp; }

	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

	@Override
	public long getButtonsDown(ButtonType type) {
		return type == ButtonType.PEN ? penButtons : type == ButtonType.AUXILIARY ? auxButtons : 0L;
	}
}
