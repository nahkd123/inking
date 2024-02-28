package io.github.nahkd123.inking.api.tablet;

import java.util.Arrays;

import io.github.nahkd123.inking.api.util.Vector2;

/**
 * <p>
 * A very simple {@link Packet} implementation.
 * </p>
 */
public class SimplePacket implements Packet {
	private long timestamp;
	private boolean penDown;
	private boolean eraser;
	private Vector2 position;
	private Vector2 tilt;
	private int rawPressure;
	private int rawHoveringDistance;
	private boolean[] penButtons;
	private boolean[] auxButtons;

	public SimplePacket(long timestamp, boolean penDown, boolean eraser, Vector2 position, Vector2 tilt, int rawPressure, int rawHoveringDistance, boolean[] penButtons, boolean[] auxButtons) {
		this.timestamp = timestamp;
		this.penDown = penDown;
		this.eraser = eraser;
		this.position = position;
		this.tilt = tilt;
		this.rawPressure = rawPressure;
		this.rawHoveringDistance = rawHoveringDistance;
		this.penButtons = penButtons;
		this.auxButtons = auxButtons;
	}

	@Override
	public long getTimestamp() { return timestamp; }

	@Override
	public Vector2 getPenPosition() { return position; }

	@Override
	public Vector2 getTilt() { return tilt; }

	@Override
	public int getRawPressure() { return rawPressure; }

	@Override
	public int getRawHoverDistance() { return rawHoveringDistance; }

	@Override
	public boolean isPenDown() { return penDown; }

	@Override
	public boolean isEraser() { return eraser; }

	@Override
	public boolean isButtonDown(ButtonType type, int index) {
		boolean[] arr = switch (type) {
		case PEN -> penButtons;
		case AUXILIARY -> auxButtons;
		default -> new boolean[0];
		};

		if (index < 0 || index >= arr.length) return false;
		return arr[index];
	}

	@Override
	public String toString() {
		return "SimplePacket [penDown=" + penDown + ", eraser=" + eraser + ", position=" + position + ", tilt=" + tilt
			+ ", rawPressure=" + rawPressure + ", rawHoveringDistance=" + rawHoveringDistance + ", penButtons="
			+ Arrays.toString(penButtons) + ", auxButtons=" + Arrays.toString(auxButtons) + "]";
	}
}
